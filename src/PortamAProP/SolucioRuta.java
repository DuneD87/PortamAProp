package PortamAProP;

/**
 * @class SolucioRuta
 * @brief Classe encarregada de guardar les diferents solucions
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;



public class SolucioRuta {


    

    /**
     * @brief Aquesta estructura requereix una explicacio mes elaborada. La idea
     * es la seguent: Guardem una coleccio de pairs, aquest pair conte un
     * character que ens diu si el candidat es origen, desti o depot, i
     * seguidament una referencia a un node, per poder comprovar pes de aresta en
     * temps constant.
     */
    private ArrayList<Pair<Character, Node>> _candidats;
    
    /*MAPA, VEHICLES Y PETICIONS*/
    private Vehicle _vehicle;//!<@brief Vehicle que realitzara la ruta
    private double _tempsEnMarxa;//!<@brief temps acumulat en atendre totes les peticions
    private double _tempsADepot;//!<@brief temps que esta el vehicle carregan
    private double _tempsTotal;//!<@brief Temps total, suma de temps en marxa i temps a depot
    private Graph _graf;//!<@brief Subgraph sobre el que treballem
    private ArrayList<Peticio> _peticio;//!<@breif Subgrup de peticions que el nostre vehicle atendra
    
    /*ESTRUCTURES ON GUARDEM LA SOLUCIO*/
    private Stack<Node> _nodes;//!<@brief Conjunt de nodes que representa la nostra ruta
    private Stack<Character> _accio;//!<@brief accio feta a cada node
    private Stack<Integer> _carrega;//!<@brief ens diu quants passatgers a carregat/descarregat a cada node
    private ArrayList<PeticioEnTramit> _peticionsTramit;//!<@brief Llista de peticions en tramit
    
    /*VARIABLES DE CONTROL*/
    private int _nPeticions;//!<@brief Numero de peticions que estem tramitan
    private int _nPeticionsTramitades;//!<@brief Numero de peticions que estan finalitzades
    private LocalTime _horaActual; //!<@brief Ens diu l'hora actual
    private int _maximEspera;//!<@brief Temps maxim d'espera que estem disposats a esperar
    private int _minimLegal;//!<@brief Temps establer per la llei que ha d'esperar minim tota peticio
    
    private Ruta _ruta;//!<@brief Ruta que fa el vehicle
    
    private double _minCarga; //!<@brief ens diu quan el vehicle ha d'anar a carregar
    
    //Afegit per Buenaventura 
    private int [] _conversio; //!<@brief ArrayList de pairs que assosia els index dels nodes del subgraf amb el graf complet ( Possible solucio per el conflicte de indexs)
    
    private StringBuilder _log;
    
    /**
     * @brief Constructor
     * @post A partir d'una ruta generada per el voraç, obtenim les diferents
     * estructures de dades, i completem la ruta, intentan millorar la solucio
     * obtenida.
     * @param r La ruta que ens dona el voraç
     * @param minimLegal Minim temps legal d'espera per atendre una peticio
     * @param maximEspera Maxim temps que els clients estan disposats a esperar
     * @param minCarga Minim de bateria que el vehicle considera apropiat abans d'anar a carregar
     */
    public SolucioRuta(Ruta r, int minimLegal, int maximEspera,double minCarga, StringBuilder log) {
       
        //Inicialitzem les diferents estructures
        _peticio = r.getSol();
        _vehicle = r.getVehicle();
        _tempsEnMarxa = 0;
        _tempsTotal = 0;
        _nPeticions = 0;
        _graf = r.getGraph();
        _conversio=r.retornarConversio();
        List<Node> nodes = new ArrayList<>(_graf.getNodeSet());
        _vehicle.cargar(50000);
        _candidats = new ArrayList<>();
        _nodes = new Stack<>();
        _horaActual = r.obtHoraPrimeraPeticio();
        _accio = new Stack<>();
        _carrega = new Stack<>();
        _tempsADepot = 0;
        _ruta = r;
        _peticionsTramit = new ArrayList<>();
        _maximEspera = minimLegal;//30 mins d'espera maxim
        _minimLegal = maximEspera; 
        _minCarga = minCarga;
        _log = log;
        _log.append("\n*********************************** INICIAN ALGORITME DE BACKTRACKING ***********************************\n" + 
                 "VEHICLE:\n" + _vehicle.toString());
         
        //Busquem la posicio del vehicle
        for (Node p : nodes) {
            if (p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                _log.append("\n****POSICIO DEL VEHICLE AFEGIDA A LA RUTA****\n");
                _nodes.push(p);
                _accio.push('P');
            }
                
        }
        
        //Creem dins de les peticions els origens i destinacions i els afegim a la llista de candidats
        for (Peticio s : _peticio) {
            s.modificarEstat(Peticio.ESTAT.ESPERA);
            Node origen = _graf.getNode(_conversio[s.origen()]);
            Node desti = _graf.getNode(_conversio[s.desti()]);
            Pair<Character, Node> p1 = new Pair('O', origen);
            _candidats.add(p1);
            _log.append("\nPeticio: " + s.identificador()
                    + "\n--Origen: " + origen.getAttribute("Nom")
                    + "\n--Desti: " + desti.getAttribute("Nom")
                    + "\n--Hora emissio: " + s.emissio()
                    + "\n--Nombre de passatgers: " + s.numPassatgers()
                    + "\n--Temps estimat: " + origen.getEdgeBetween(desti).getAttribute("pes") + " minuts" );
            Pair<Character, Node> p2 = new Pair('D', desti);
            _candidats.add(p2);
            PeticioEnTramit p = new PeticioEnTramit(s);
            _peticionsTramit.add(p);
           
        }
        _log.append("\nNOMBRE DE PETICIONS A ATENDRE: " + _peticio.size());
        
        //Busquem els depots i els afegim a la llista de candidats
        int nDepots = 0;
        for (Node p : nodes) {
            String s = p.getAttribute("Tipus");
            if (p.getAttribute("Tipus") == "Depot" && !p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                Pair<Character, Node> depot = new Pair('P', p);
                _candidats.add(depot);
                nDepots++;
            }
        }
        _log.append("\nNOMBRE DE DEPOTS: " + nDepots +
                "\n*********************************************************************************************************\n");
    }
    
    /**
     * @brief Constructor de copia
     * @pre ---
     * @post S'ha construit una nova SolucioRuta a partir de la solucio anterior \n
     * Es fa copia de totes aquelles estructures que guarden solucions parcials al
     * problema, la resta, copiem la seva referencia.
     * @param sol Ens dona la solucio anterior
     */
    public SolucioRuta(SolucioRuta sol) {
        _peticio = new ArrayList<>(sol._peticio);
        _peticionsTramit = new ArrayList<>();
        for (PeticioEnTramit p : sol._peticionsTramit) {
            PeticioEnTramit a = new PeticioEnTramit(p);
            _peticionsTramit.add(a);
        }
        _candidats = sol._candidats;
        _conversio = sol._conversio;
        _tempsEnMarxa = sol._tempsEnMarxa;
        _nPeticions = sol._nPeticions;
        _nodes = (Stack<Node>)sol._nodes.clone();
        _vehicle = sol._vehicle;
        _graf = sol._graf;
        _horaActual = sol._horaActual;
        _accio = (Stack<Character>)sol._accio.clone();
        _carrega = (Stack<Integer>)sol._carrega.clone();
        _tempsADepot = sol._tempsADepot;
        _ruta = sol._ruta;
        _tempsTotal = sol._tempsTotal;
        _log = sol._log;
    }

    /**
     * @brief Candidat acceptable
     * @pre 0 <= iCan.actual() < nNodes - 1 
     * @post Ens diu si el candidat es acceptable: \n
     * Determinem el tipus de candidat del que es tracta ( origen, desti, depot )
     * i mirem si pot arribar al node. En cas afirmatiu, tractem el candidat per el
     * seu tipus i determinem si es acceptable
     */
    public boolean acceptable(CandidatRuta iCan) {
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _candidats.get(iCan.actual()).getValue();
        boolean acceptable = false;
       
        double temps;
        if (_nodes.lastElement() != p)
            temps = (Double)_nodes.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;

        if (temps < _vehicle.carregaRestant()) {
            // Podem arribar, mirem si el candidat es acceptable
            switch (tipus) {
                case 'O':
                    acceptable = origenAcceptable(iCan,temps);
                    break;
                case 'D':
                    acceptable = destiAcceptable(iCan, p);
                    break;
                case 'P':
                    acceptable = depotAcceptable(iCan);
                    break;
            }
        }
        return acceptable;
    }
   
    
    /**
     * @brief Ens diu si considerem el candidat origen com a acceptable, per
     * aixo s'han de cumplir dos condicions: \n
     * -Tenim prou espai per carregar els clients que esperen \n
     * -La bateria del vehicle es superior al 50% \n
     * -L'hora actual es inferior a l'hora de l'emissio + el minim temps legal d'espera + el maxim temps que el client vol esperar + el temps d'arribada \n
     */
    private boolean origenAcceptable(CandidatRuta iCan, double temps) {
        double mitjaBat = _vehicle.carregaTotal() * _minCarga;
        PeticioEnTramit actual = _peticionsTramit.get(iCan.actual()/2);
        return actual.nPassatgers() < (_vehicle.nPassTotal() -_vehicle.nPassatgers())
                && _vehicle.carregaRestant() > mitjaBat
                && actual.obtenirEstat() == PeticioEnTramit.ESTAT.ESPERA
                && _horaActual.plusMinutes((int)temps).isBefore(actual.horaEmissio().plusMinutes((int)(_minimLegal + _maximEspera)));
    }

    /**
     * @brief Ens diu si considerem el candidat desti com a acceptable, per aixo
     * s'ha de cumplir:\n 
     * Els passatgers que portem hagin de baixar al node
     */
    private boolean destiAcceptable(CandidatRuta iCan, Node p) {
        //double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;//Justifico repeticio de codi perque se que l'esquema global funcionara be, pero els individuals poder no
        return _conversio[_peticionsTramit.get(iCan.actual()/2).obtenirDesti()] == p.getIndex()
            && _peticionsTramit.get(iCan.actual()/2).obtenirEstat() == PeticioEnTramit.ESTAT.ENTRANSIT;
    }

    /**
     * @brief Ens diu si considerem el candidat depot com aceptable, per aixo
     * s'ha de cumplir:\n 
     * No hi ha cap peticio en curs, i la bateria del vehicle
     * esta per sota del factor de carrega critic
     */
    private boolean depotAcceptable(CandidatRuta iCan) {
        double mitjaBat = _vehicle.carregaTotal() * _minCarga;
        return _vehicle.carregaRestant() < mitjaBat
                && _nPeticions == 0
                && _accio.lastElement() != 'P';
    }

    /**
     * @brief Inicialitza el candidat i possa com a maxim el maxim del vector de
     * candidats. Els nostres candidats son:\n 
     * Origen: Origen d'una peticio \n
     * Desti: Desti d'una peticio \n
     * Depot: Punt de recarrega \n
     */
    public CandidatRuta iniCan() {
        return new CandidatRuta(_candidats.size() - 1);
    }

    /**
     * @brief Anotem candidat
     * @post S'ha anotat el node a la ruta, s'ha actualitzat el cost total i
     * s'ha decrementat la bateria del vehicle. *LA POST ES FA SEMPRE* \n
     * \n
     * Independenment de la postcondicio hem de tractar 3 casos: \n
     * \n
     * Origen i desti: Modifiquem el nombre de passatges del vehicle i
     * actualitzem el rellotge global. \n
     * \n
     * Depot: Carreguem el cotxe fins carrega completa i actualitzem el rellotge
     * global. \n
     */
    public void anotar(CandidatRuta iCan) {
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _candidats.get(iCan.actual()).getValue();
        double temps;
        
        if (p != _nodes.lastElement())
            temps = _nodes.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;
        
        _nodes.push(_candidats.get(iCan.actual()).getValue());
        _accio.push(tipus);
        
        _vehicle.descarga(temps);
        _tempsEnMarxa += temps;
        _tempsTotal += temps;
         
        
        switch (tipus) {
            case 'O':
                _horaActual = _horaActual.plusMinutes((long)temps);
                _nPeticions++;
                _vehicle.ModificarPassatgers(_peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _carrega.push(_peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _peticionsTramit.get(iCan.actual()/2).actualitzarEstat(PeticioEnTramit.ESTAT.ENTRANSIT);
                if (_horaActual.isBefore(_peticionsTramit.get(iCan.actual() /2).horaEmissio())) { //Si l'hora actual es abans que l'hora d' emissio
                    _horaActual = _peticionsTramit.get(iCan.actual() / 2).horaEmissio();//Esperem fins l'hora d emissio
                    
                }
               
                int diff = Math.abs(_minimLegal - (int)temps);
                _peticionsTramit.get(iCan.actual()/2).assignarRecollida(diff);
                _horaActual = _horaActual.plusMinutes(diff);
                break;
            case 'D':
                _horaActual = _horaActual.plusMinutes((long)temps);
                _nPeticions--;
                _nPeticionsTramitades++;
                _vehicle.ModificarPassatgers(-1 *  _peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _carrega.push(-1* _peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _peticionsTramit.get(iCan.actual()/2).actualitzarEstat(PeticioEnTramit.ESTAT.FINALITZADA);
                _peticionsTramit.get(iCan.actual()/2).assignarArribada((int)temps);
                 
                break;
            case 'P':
                double carregaCompleta = _vehicle.carregaTotal() - _vehicle.carregaRestant();
                _vehicle.cargar(carregaCompleta);
                _carrega.push((int)carregaCompleta);//bad decisions.. 
                _horaActual = _horaActual.plusMinutes((long)carregaCompleta);
                _tempsADepot += carregaCompleta;
                _tempsTotal += carregaCompleta;
                break;
        }
    }

    /**
     * @brief Desanotem candidat
     * @post S'ha eliminat l'ultim node de la ruta, s'ha actualitzat el cost
     * total i s'ha incrementat la bateria del cotxe \n
     * \n
     * Com en el cas d'anotar, aqui tambe tenim els 3 casos, complementaris al anotar \n
     */
    public void desanotar(CandidatRuta iCan) {
        char tipus = _accio.pop();
        Node p = _nodes.pop();
         
        double temps;
        if (p != _nodes.lastElement())
            temps = _nodes.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;
        
        _vehicle.cargar(temps);
        _tempsEnMarxa -= temps;
        _tempsTotal -= temps;
        
         
         
        switch (tipus) {
            case 'O':
                _horaActual = _horaActual.minusMinutes((long)temps);
                _nPeticions--;
                _vehicle.ModificarPassatgers(-1* _peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _carrega.pop();
                _peticionsTramit.get(iCan.actual()/2).actualitzarEstat(PeticioEnTramit.ESTAT.ESPERA);
                
                if (_horaActual.isAfter(_peticionsTramit.get(iCan.actual() /2).horaEmissio())) { //Si l'hora actual es abans que l'hora d' emissio
                    _horaActual = _peticionsTramit.get(iCan.actual() / 2).horaEmissio();//Esperem fins l'hora d emissio
                   
                }
             
                int diff = Math.abs(_minimLegal - (int)temps);
                _peticionsTramit.get(iCan.actual()/2).assignarRecollida(-1*diff);
                _horaActual = _horaActual.minusMinutes(diff);
                break;
            case 'D':
                _horaActual = _horaActual.minusMinutes((long)temps);
                _nPeticions++;
                _nPeticionsTramitades--;
                _vehicle.ModificarPassatgers(_peticionsTramit.get(iCan.actual()/2).nPassatgers());
                _carrega.pop();
                _peticionsTramit.get(iCan.actual()/2).actualitzarEstat(PeticioEnTramit.ESTAT.ENTRANSIT);
                _peticionsTramit.get(iCan.actual()/2).assignarArribada(-1*(int)temps);
                
                break;
            case 'P':
                double carregaCompleta = (double)_carrega.pop();
                _vehicle.descarga(carregaCompleta);
                _tempsADepot -= carregaCompleta;
                _tempsTotal -= carregaCompleta;
                _horaActual.minusMinutes((long)carregaCompleta);
                break;
        }

    }
    
    /**
     * @brief Solucio completa
     * @post Ens diu si el nombre de peticions tramitades es igual al nombre de 
     * peticions que teniem en una primera instancia
     */
    public boolean completa() {
      
        return _nPeticionsTramitades == (_peticio.size());
    }
    
    /**
     * @brief Es millor
     * @post Ens diu si el temps que ha estat en marxa el vehicle en aquesta solucio
     * es inferior al temps que ha estat en marxa el vehicle a la solucio anterior.
     */
    public boolean esMillor(SolucioRuta optim) {
        return _tempsTotal < optim._tempsTotal;
    }
    
    /**
     * @brief Guarda el resultat a ruta
     * @pre ---
     * @post S'han afegit el conjunt de nodes, accions, carrega i peticions a la ruta.
     * A mes, hem afegit l'hora en que el vehicle ha finalitzat, el temps que ha estat en marxa
     * i el temps que ha estat carregant.
     */
    public void finalitzar() {
       
        _ruta.completarRuta(_nodes,_accio,_carrega,_peticio,_horaActual,_tempsEnMarxa,_tempsADepot,_peticionsTramit,_log);
        
    }
}
