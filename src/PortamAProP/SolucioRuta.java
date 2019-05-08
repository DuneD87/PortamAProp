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


    private ArrayList<Solicitud> _solicituds;//@breif Subgrup de solicituds que el nostre vehicle atendra

    /**
     * @brief Aquesta estructura requereix una explicacio mes elaborada. La idea
     * es la seguent: Guardem una coleccio de pairs, aquest pair conte un
     * character que ens diu si el candidat es origen, desti o depot, i
     * seguidament una referencia a un node, per poder comprovar pes de aresta en
     * temps constant.
     */
    private ArrayList<Pair<Character, Node>> _candidats;
    private Vehicle _vehicle;//@brief Vehicle que realitzara la ruta
    private double _tempsEnMarxa;//@brief temps acumulat en atendre totes les peticions
    private double _tempsADepot;//@brief temps que esta el vehicle carregan
    private Graph _graf;//@brief Subgraph sobre el que treballem
    
    private Stack<Node> _nodes;//@brief Conjunt de nodes que representa la nostra ruta
    private Stack<Character> _accio;//@brief accio feta a cada node
    private Stack<Integer> _carrega;//@brief ens diu quants passatgers a carregat/descarregat a cada node
    
    private int _nPeticions;//@brief Numero de peticions que estem tramitan
    private int _nPeticionsTramitades;//@brief Numero de peticions que estan finalitzades
    private LocalTime _horaActual; //@brief Ens diu l'hora actual
    
    private Ruta _ruta;//@brief Ruta que fa el vehicle
    
    private static final double FACTOR_CARREGA_CRITIC = 0.5; //@brief Constant que ens diu quan el vehicle ha de carregar
    
    //Afegit per Buenaventura 
    private int [] _conversio; //@brief ArrayList de pairs que assosia els index dels nodes del subgraf amb el graf complet ( Possible solucio per el conflicte de indexs)

    /**
     * @brief Constructor
     * @post A partir d'una ruta generada per el voraç, obtenim les diferents
     * estructures de dades, i completem la ruta, intentan millorar la solucio
     * obtenida.
     */
    public SolucioRuta(Ruta r,LocalTime horaActual) {
       
        //Inicialitzem les diferents estructures
        _solicituds = r.getSol();
        _vehicle = r.getVehicle();
        _tempsEnMarxa = 0;
        _nPeticions = 0;
        _graf = r.getGraph();
        _conversio=r.retornarConversio();
        List<Node> nodes = new ArrayList<>(_graf.getNodeSet());
        _vehicle.cargar(50000);
        _candidats = new ArrayList<>();
        _nodes = new Stack<>();
        _horaActual = horaActual;//comencem a les 12
        _accio = new Stack<>();
        _carrega = new Stack<>();
        _tempsADepot = 0;
        _ruta = r;
         System.out.println("*********************************** INICIAN ALGORITME DE BACKTRACKING ***********************************\n" + 
                 "VEHICLE:\n" + _vehicle.toString());
         
        //Busquem la posicio del vehicle
        for (Node p : nodes) {
            if (p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                System.out.println("****POSICIO DEL VEHICLE AFEGIDA A LA RUTA****\n");
                _nodes.push(p);
            }
                
        }
        
        //Creem dins de les solicituds els origens i destinacions i els afegim a la llista de candidats
        for (Solicitud s : _solicituds) {
            s.setEstat(Solicitud.ESTAT.ESPERA);
            Pair<Character, Node> p1 = new Pair('O', _graf.getNode((_conversio[s.Origen()])));
            _candidats.add(p1);
            System.out.println("SOLICITUD: " + "Origen: " + _graf.getNode(_conversio[s.Origen()]).getAttribute("Nom")
                    + " Desti: " + _graf.getNode(_conversio[s.Desti()]).getAttribute("Nom")
                    + " Hora emissio: " + s.Emisio());
            Pair<Character, Node> p2 = new Pair('D', _graf.getNode((_conversio[s.Desti()])));
            _candidats.add(p2);
           
        }
        System.out.println("NOMBRE DE SOLICITUDS A ATENDRE: " + _solicituds.size());
        
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
        _solicituds = sol._solicituds;
        _candidats = sol._candidats;
        _conversio = sol._conversio;
        _tempsEnMarxa = sol._tempsEnMarxa;
        _nPeticions = sol._nPeticions;
        _nodes = (Stack<Node>)sol._nodes.clone();
        _vehicle = sol._vehicle;
        _graf = sol._graf;
        _horaActual = sol._horaActual;
        _accio = sol._accio;
        _carrega = sol._carrega;
        _tempsADepot = sol._tempsADepot;
        _ruta = sol._ruta;
    }

    /**
     * @brief Candidat acceptable
     * @pre 0 <= iCan.actual() <= nNodes - 1 
     * @post Ens diu si el candidat es acceptable
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
     * ->Tenim prou espai per carregar els
     * clients que esperen. 
     * ->La bateria del vehicle es superior al 50%
     */
    private boolean origenAcceptable(CandidatRuta iCan, double temps) {
        double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;
        Solicitud actual = _solicituds.get(iCan.actual()/2);
        return actual.NumPassatgers() < (_vehicle.nPassTotal() -_vehicle.nPassatgers())
                && _vehicle.carregaRestant() > mitjaBat
                && actual.getEstat() == Solicitud.ESTAT.ESPERA;
    }

    /**
     * @brief Ens diu si considerem el candidat desti com a acceptable, per aixo
     * s'ha de cumplir: 
     * ->Els passatgers que portem hagin de baixar al node
     */
    private boolean destiAcceptable(CandidatRuta iCan, Node p) {
        //double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;//Justifico repeticio de codi perque se que l'esquema global funcionara be, pero els individuals poder no
        return _conversio[_solicituds.get(iCan.actual()/2).Desti()] == p.getIndex()
            && _solicituds.get(iCan.actual()/2).getEstat() == Solicitud.ESTAT.ENTRANSIT;
    }

    /**
     * @brief Ens diu si considerem el candidat depot com aceptable, per aixo
     * s'ha de cumplir: 
     * ->No hi ha cap peticio en curs, i la bateria del vehicle
     * esta per sota del factor de carrega critic
     */
    private boolean depotAcceptable(CandidatRuta iCan) {
        double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;
        return _vehicle.carregaRestant() < mitjaBat
                && _nPeticions == 0;
    }

    /**
     * @brief Inicialitza el candidat i possa com a maxim el maxim del vector de
     * candidats. Els nostres candidats son: 
     * -Origen: Origen d'una peticio
     * -Desti: Desti d'una peticio -Depot: Punt de recarrega
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
     * -> Origen i desti: Modifiquem el nombre de passatges del vehicle i
     * actualitzem el rellotge global.
     *
     * -> Depot: Carreguem el cotxe durant 30min i actualitzem el rellotge
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
        
        Solicitud actual =  _solicituds.get(iCan.actual()/2);
        
        switch (tipus) {
            case 'O':
                _nPeticions++;
                _vehicle.ModificarPassatgers(actual.NumPassatgers());
                _carrega.push(actual.NumPassatgers());
                actual.setEstat(Solicitud.ESTAT.ENTRANSIT);
                
                if (_horaActual.isBefore(actual.Emisio().plusMinutes((long) temps))) {//Si l'hora actual es abans que l'hora d emisio + el temps d'arribada
                    _horaActual = _horaActual.plusMinutes(actual.Emisio().toSecondOfDay()/60 + (long)temps);
                    if (_horaActual.isBefore(actual.Emisio().plusMinutes(15))) {//Si l'hora actual es abans que l'hora d'emisio + el temps d'arribada + 15
                        _horaActual = _horaActual.plusMinutes(15);//millorable 
                    }
                }
                actual.assignarHoraRecollida(_horaActual);
                break;
            case 'D':
                _nPeticions--;
                _nPeticionsTramitades++;
                _vehicle.ModificarPassatgers(-1 * actual.NumPassatgers());
                _carrega.push(-1*actual.NumPassatgers());
                actual.setEstat(Solicitud.ESTAT.FINALITZADA);
                actual.AssignarArribada(_horaActual);
                break;
            case 'P':
                double carregaCompleta = _vehicle.carregaTotal() - _vehicle.carregaRestant();
                _vehicle.cargar(carregaCompleta);
                _carrega.push((int)carregaCompleta);//bad decisions.. 
                _tempsADepot += carregaCompleta;
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
        
        Solicitud actual = _solicituds.get(iCan.actual()/2);

        switch (tipus) {
            case 'O':
                _nPeticions--;
                _vehicle.ModificarPassatgers(-1*actual.NumPassatgers());
                _carrega.pop();
                actual.setEstat(Solicitud.ESTAT.ESPERA);
                 if (_horaActual.isAfter(actual.Emisio().plusMinutes((long) temps))) {//Si l'hora actual es abans que l'hora d emisio + el temps d'arribada
                    _horaActual = _horaActual.minusMinutes(actual.Emisio().toSecondOfDay()/60 + (long)temps);
                    if (_horaActual.isAfter(actual.Emisio().plusMinutes(15))) {//Si l'hora actual es abans que l'hora d'emisio + el temps d'arribada + 15
                        _horaActual = _horaActual.minusMinutes(15);//millorable 
                    }
                }
                actual.assignarHoraRecollida(null);
                break;
            case 'D':
                _nPeticions++;
                _nPeticionsTramitades--;
                _vehicle.ModificarPassatgers(actual.NumPassatgers());
                _carrega.pop();
                actual.setEstat(Solicitud.ESTAT.ENTRANSIT);
                actual.AssignarArribada(null);
                break;
            case 'P':
                double carregaCompleta = (double)_carrega.pop();
                _vehicle.descarga(carregaCompleta);
                _tempsADepot -= carregaCompleta;
                break;
        }

    }
    
    /**
     * @brief Solucio completa
     * @post Ens diu si el nombre de peticions tramitades es igual al nombre de 
     * peticions que teniem en una primera instancia
     */
    public boolean completa() {
        return _nPeticionsTramitades == (_solicituds.size());
    }
    
    /**
     * @brief Pot ser millor
     * @post Sempre potser millor, tot es precios
     */
    public boolean potSerMillor(SolucioRuta optim) {
        return true;
    }
    
    /**
     * @brief Es millor
     * @post Ens diu si el cost de de la solucio actual, es inferior al cost 
     * de la solucio anterior.
     */
    public boolean esMillor(SolucioRuta optim) {
        return _tempsEnMarxa < optim._tempsEnMarxa;
    }
    
    /**
     * @brief Ruta efectuada
     * @pre ---
     * @post Ens dona una pila de nodes que descriu el recorregut del vehicle
     * @return 
     */
    public Stack<Node> obtSolucio() {
        return _nodes;
    }
    
    /**
     * @brief Temps total
     * @pre ---
     * @post Ens diu el temps total que ha tardat el vehicle en efectuar la ruta
     */
    public double obtCost() {
        return _tempsEnMarxa;
    }
    
    /**
     * @brief Hora d'acabada
     * @pre ---
     * @post Ens diu l'hora en que ha finalitzat el vehicle
     */
    public LocalTime horaArribada() {
        return _horaActual;
    }
    
    /**
     * @brief Guarda el resultat a ruta
     * @pre ---
     * @post S'han guardat les diferents estructures a ruta
     */
    public void finalitzar() {
        _ruta.completarRuta(_nodes,_accio,_carrega,_solicituds,_horaActual,_tempsEnMarxa,_tempsADepot);
    }
}
