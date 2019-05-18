package PortamAProP;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * @class SolucioRuta
 * @brief Classe encarregada de guardar les diferents solucions
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class SolucioRuta {


    

    /**
     * @brief Aquesta estructura requereix una explicacio mes elaborada. La idea
     * es la seguent: Guardem una coleccio de pairs, aquest pair conte un
     * character que ens diu si el candidat es origen, desti o depot, i
     * seguidament una referencia a un node, per poder comprovar pes de aresta en
     * temps constant.
     */
    private ArrayList<Pair<Character, Node>> _candidats;
    
    /**MAPA, VEHICLES Y PETICIONS*/
    private Vehicle _vehicle;//@brief Vehicle que realitzara la ruta
    private double _tempsEnMarxa;//@brief temps acumulat en atendre totes les peticions
    private double _tempsADepot;//@brief temps que esta el vehicle carregan
    private double _tempsTotal;//@brief Temps total, suma de temps en marxa i temps a depot
    private Graph _graf;//@brief Subgraph sobre el que treballem
    private ArrayList<Peticio> _peticio;//@breif Subgrup de peticions que el nostre vehicle atendra
    
    /**ESTRUCTURES ON GUARDEM LA SOLUCIO*/
    private Stack<Node> _nodes;//@brief Conjunt de nodes que representa la nostra ruta
    private Stack<Character> _accio;//@brief accio feta a cada node
    private Stack<Integer> _carrega;//@brief ens diu quants passatgers a carregat/descarregat a cada node
    private ArrayList<PeticioEnTramit> _peticionsTramit;//@brief Llista de peticions en tramit
    
    /**VARIABLES DE CONTROL*/
    private int _nPeticions;//@brief Numero de peticions que estem tramitan
    private int _nPeticionsTramitades;//@brief Numero de peticions que estan finalitzades
    private LocalTime _horaActual; //@brief Ens diu l'hora actual
    private int _maximEspera;//@brief Temps maxim d'espera que estem disposats a esperar
    private int _minimLegal;//@brief Temps establer per la llei que ha d'esperar minim tota peticio
    
    private Ruta _ruta;//@brief Ruta que fa el vehicle
    
    private static final double FACTOR_CARREGA_CRITIC = 0.9; //@brief Constant que ens diu quan el vehicle ha de carregar
    
    //Afegit per Buenaventura 
    private int [] _conversio; //@brief ArrayList de pairs que assosia els index dels nodes del subgraf amb el graf complet ( Possible solucio per el conflicte de indexs)

    /**
     * @brief Constructor
     * @post A partir d'una ruta generada per el voraç, obtenim les diferents
     * estructures de dades, i completem la ruta, intentan millorar la solucio
     * obtenida.
     * @param r La ruta que ens dona el voraç
     * @param minimLegal Minim temps legal d'espera per atendre una peticio
     * @param maximEspera Maxim temps que els clients estan disposats a esperar
     */
    public SolucioRuta(Ruta r, int minimLegal, int maximEspera) {
       
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
         System.out.println("*********************************** INICIAN ALGORITME DE BACKTRACKING ***********************************\n" + 
                 "VEHICLE:\n" + _vehicle.toString());
         
        //Busquem la posicio del vehicle
        for (Node p : nodes) {
            if (p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                System.out.println("****POSICIO DEL VEHICLE AFEGIDA A LA RUTA****\n");
                _nodes.push(p);
                _accio.push('P');
            }
                
        }
        
        //Creem dins de les peticions els origens i destinacions i els afegim a la llista de candidats
        for (Peticio s : _peticio) {
            s.modificarEstat(Peticio.ESTAT.ESPERA);
            Pair<Character, Node> p1 = new Pair('O', _graf.getNode((_conversio[s.origen()])));
            _candidats.add(p1);
            System.out.println("Peticio: " + "Origen: " + _graf.getNode(_conversio[s.origen()]).getAttribute("Nom")
                    + " Desti: " + _graf.getNode(_conversio[s.desti()]).getAttribute("Nom")
                    + " Hora emissio: " + s.emissio());
            Pair<Character, Node> p2 = new Pair('D', _graf.getNode((_conversio[s.desti()])));
            _candidats.add(p2);
            PeticioEnTramit p = new PeticioEnTramit(s);
            _peticionsTramit.add(p);
           
        }
        System.out.println("NOMBRE DE PETICIONS A ATENDRE: " + _peticio.size());
        
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
        System.out.println("NOMBRE DE DEPOTS: " + nDepots +
                "\n*********************************************************************************************************");
    }
    
    /**
     * @brief Constructor de copia
     * @pre ---
     * @post S'ha construit una nova SolucioRuta a partir de la solucio anterior
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
        //_horaActual = new LocalTime(sol._horaActual);
        _accio = (Stack<Character>)sol._accio.clone();
        _carrega = sol._carrega;
        _tempsADepot = sol._tempsADepot;
        _ruta = sol._ruta;
    }

    /**
     * @brief Candidat acceptable
     * @pre 0 <= iCan.actual() <= nNodes - 1 
     * @post Ens diu si el candidat es acceptable:
     * Determinem el tipus de candidat del que es tracta ( origen, desti, depot )
     * i mirem si pot arribar al node. En cas afirmatiu, tractem el candidat per el
     * seu tipus i determinem si es acceptable
     */
    public boolean acceptable(CandidatRuta iCan) {
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _candidats.get(iCan.actual()).getValue();
        boolean acceptable = false;
        /**
         * Mirem si podem arribar al node, tenicament el voraç ja ho comprova,
         * pero fem la comprovacio igualment (cas raro en que el voraç trobi una
         * ruta que el bactracking no trobi ?
         */
       
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
     * aixo s'han de cumplir dos condicions: 
     * Tenim prou espai per carregar els
     * clients que esperen. 
     * La bateria del vehicle es superior al 50%.
     * L'hora actual es inferior a l'hora de l'emissio + el minim temps legal d'espera 
     * + el maxim temps que el client vol esperar + el temps d'arribada.
     */
    private boolean origenAcceptable(CandidatRuta iCan, double temps) {
        double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;
        PeticioEnTramit actual = _peticionsTramit.get(iCan.actual()/2);
        return actual.nPassatgers() < (_vehicle.nPassTotal() -_vehicle.nPassatgers())
                && _vehicle.carregaRestant() > mitjaBat
                && actual.obtenirEstat() == PeticioEnTramit.ESTAT.ESPERA
                ;
    }

    /**
     * @brief Ens diu si considerem el candidat desti com a acceptable, per aixo
     * s'ha de cumplir: 
     * Els passatgers que portem hagin de baixar al node
     */
    private boolean destiAcceptable(CandidatRuta iCan, Node p) {
        //double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;//Justifico repeticio de codi perque se que l'esquema global funcionara be, pero els individuals poder no
        return _conversio[_peticionsTramit.get(iCan.actual()/2).obtenirDesti()] == p.getIndex()
            && _peticionsTramit.get(iCan.actual()/2).obtenirEstat() == PeticioEnTramit.ESTAT.ENTRANSIT;
    }

    /**
     * @brief Ens diu si considerem el candidat depot com aceptable, per aixo
     * s'ha de cumplir: 
     * No hi ha cap peticio en curs, i la bateria del vehicle
     * esta per sota del factor de carrega critic
     */
    private boolean depotAcceptable(CandidatRuta iCan) {
        double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;
        return _vehicle.carregaRestant() < mitjaBat
                && _nPeticions == 0
                && _accio.lastElement() != 'P';
    }

    /**
     * @brief Inicialitza el candidat i possa com a maxim el maxim del vector de
     * candidats. Els nostres candidats son: 
     * Origen: Origen d'una peticio
     * Desti: Desti d'una peticio 
     * Depot: Punt de recarrega
     */
    public CandidatRuta iniCan() {
        return new CandidatRuta(_candidats.size() - 1);
    }

    /**
     * @brief Anotem candidat
     * @post S'ha anotat el node a la ruta, s'ha actualitzat el cost total i
     * s'ha decrementat la bateria del vehicle. *LA POST ES FA SEMPRE*
     *
     * Independenment de la postcondicio hem de tractar 3 casos:
     *
     * Origen i desti: Modifiquem el nombre de passatges del vehicle i
     * actualitzem el rellotge global.
     *
     * Depot: Carreguem el cotxe durant 30min i actualitzem el rellotge
     * global.
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
                if (_horaActual.isAfter(_peticionsTramit.get(iCan.actual()/2).horaEmissio().plusMinutes(_minimLegal))) {
                    _horaActual = _horaActual.plusMinutes(_minimLegal);
                }
                int diff = _minimLegal + (int)temps;
                _peticionsTramit.get(iCan.actual()/2).assignarRecollida(diff);
                
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
     * total i s'ha incrementat la bateria del cotxe
     *
     * Com en el cas d'anotar, aqui tambe tenim els 3 casos, complementaris al anotar.
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
                
                if (_horaActual.isAfter(_peticionsTramit.get(iCan.actual()/2).horaEmissio().plusMinutes(_minimLegal))) {
                    _horaActual = _horaActual.minusMinutes(_minimLegal);
                }
                _peticionsTramit.get(iCan.actual()/2).assignarRecollida(-1*(int)temps);
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
     * @brief Pot ser millor
     * @post NO IMPLEMENTAT
     */
    public boolean potSerMillor(SolucioRuta optim) {
        return true;
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
        System.out.println("***PETICIONS TRAMITADES AL BACKTRACKING***");
        for (PeticioEnTramit p : _peticionsTramit)
            System.out.println(p.toString());
        _ruta.completarRuta(_nodes,_accio,_carrega,_peticio,_horaActual,_tempsEnMarxa,_tempsADepot,_peticionsTramit);
        
    }
}
