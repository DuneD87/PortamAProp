package PortamAProP;

import java.util.ArrayList;
import java.util.Stack;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * @class SolucioRuta
 * @brief Classe encarregada de guardar les diferents solucions
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class SolucioRuta {

    ArrayList<Node> _nodes; //@brief Subgrup de nodes que el nostre vehicle atendra
    ArrayList<Solicitud> _solicituds;//@breif Subgrup de solicituds que el nostre vehicle atendra

    /**
     * @brief Aquesta estructura requereix una explicacio mes elaborada. La idea
     * es la seguent: Guardem una coleccio de pairs, aquest pair conte un
     * character que ens diu si el candidat es origen, desti o depot, i
     * seguidament una referencia a un node, per si en el cas de que es depot,
     * poder obtenir la capacitat actual del punt de carrega.
     */
    
    //TODO, comprovar que tenim lloc al depot, encara que si treballem amb un 
    //vehicle, sempre tindrem lloc ?
    
    ArrayList<Pair<Character, Node>> _candidats;
    Vehicle _vehicle;//@brief Vehicle que realitzara la ruta
    double _cost;//@brief temps acumulat en atendre totes les peticions
    Graph _graf;//@brief Subgraph sobre el que treballem
    Stack<Node> _ruta;//@brief Conjunt de nodes que representa la nostra ruta
    private int _nPeticions;//@brief Numero de peticions que estem tramitan
    private int _nPeticionsTramitades;//@brief Numero de peticions que estan finalitzades
    
    static final double FACTOR_CARREGA_CRITIC = 0.5; //@brief Constant que ens diu quan el vehicle ha de carregar
    
    //Afegit per Buenaventura 
    private int [] _conversio; //@brief ArrayList de pairs que assosia els index dels nodes del subgraf amb el graf complet ( Possible solucio per el conflicte de indexs)

    /**
     * @brief Constructor
     * @post A partir d'una ruta generada per el voraç, obtenim les diferents
     * estructures de dades, i completem la ruta, intentan millorar la solucio
     * obtenida.
     */
    public SolucioRuta(Ruta r, Graph g) {

        //NO COINCIDEIX CANDIDAT AMB SOLICITUD
        _solicituds = r.getSol();
        _vehicle = r.getVehicle();
        _cost = 0;
        _nPeticions = 0;
        _graf = r.getGraph();
        _conversio=r.retornarConversio();
        _nodes = new ArrayList<>(_graf.getNodeSet());
        System.out.println("*********************************** INICIAN ALGORITME DE BACKTRACKING ***********************************\n" + 
                 "VEHICLE:\n" + _vehicle.toString());
        _vehicle.cargar(50000);
        _candidats = new ArrayList<>();
        _ruta = new Stack<>();
        _graf.display();
        for (Node p : _nodes) {
            if (p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                System.out.println("****POSICIO DEL VEHICLE AFEGIDA A LA RUTA****\n");
                _ruta.push(p);
            }
                
        }
        for (Solicitud s : _solicituds) {
            s.setEstat(Solicitud.ESTAT.ESPERA);
           /*
            System.out.println("Origen en el graf: "+ s.Origen()+ " En el subgraf: " + _conversio[s.Origen()]);
             System.out.println("Desti en el graf: "+ s.Desti()+ " En el subgraf: " + _conversio[s.Desti()]);
              System.out.println("Els nodes existeixen Origen: " + _graf.getNode(_conversio[s.Origen()]).getIndex());
              System.out.println("Desti: " + _graf.getNode(_conversio[s.Desti()]).getIndex());
              System.out.println("=================NODES DEL SUBGRAF");
              for(Node n: _graf){
                  System.out.println("Node "+n+ " Index " + n.getIndex());
              }
            */
           
            Pair<Character, Node> p1 = new Pair('O', _graf.getNode((_conversio[s.Origen()])));
            _candidats.add(p1);
            System.out.println("SOLICITUD: " + "Origen: " + _graf.getNode(_conversio[s.Origen()]).getAttribute("Nom")
                    + " Desti: " + _graf.getNode(_conversio[s.Desti()]).getAttribute("Nom"));
            Pair<Character, Node> p2 = new Pair('D', _graf.getNode((_conversio[s.Desti()])));
            _candidats.add(p2);
           
        }
        System.out.println("NOMBRE DE SOLICITUDS A ATENDRE: " + _solicituds.size());
        int nDepots = 0;
        for (Node p : _nodes) {
            String s = p.getAttribute("Tipus");
            if (p.getAttribute("Tipus") == "Depot" && !p.getId().equals(Integer.toString(_vehicle.nodeInicial()))) {
                Pair<Character, Node> depot = new Pair('P', p);
                _candidats.add(depot);
                nDepots++;
            }
        }
        //System.out.println("TAMANY DELS CANDIDATS: " + _candidats.size());
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
        _cost = sol._cost;
        _nPeticions = sol._nPeticions;
        _nodes = sol._nodes;
        _ruta = (Stack<Node>)sol._ruta.clone();
        _vehicle = sol._vehicle;
        _graf = sol._graf;
    }

    /**
     * @brief Candidat acceptable
     * @pre 0 <= iCan.actual() <= nNodes - 1 
     * @post Ens diu si el candidat es acceptable
     */
    public boolean acceptable(CandidatRuta iCan) {
        //System.out.println("TAMANY DE LA PILA: " + _ruta.size());
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _candidats.get(iCan.actual()).getValue();
        boolean acceptable = false;
        //if (_ruta.lastElement() == p) return false;//workaround
        /**
         * Mirem si podem arribar al node, tenicament el voraç ja ho comprova,
         * pero fem la comprovacio igualment (cas raro en que el voraç trobi una
         * ruta que el bactracking no trobi ?
         */
        //System.out.println("NOMBRE DE PETICIONS TRAMITADES : " + _nPeticionsTramitades + " NOMBRE DE SOLICITUDS TOTALS: " + _solicituds.size());
       
        double temps;
        //System.out.println("Punt actual: " + p.getIndex());
        //System.out.println("Punt actual: " + p.getAttribute("Nom") + "-" + tipus + " Punt anterior: " + _ruta.lastElement().getAttribute("Nom") + " pes: " + " Carrega restant: " + _vehicle.carregaRestant());
        if (_ruta.lastElement() != p)
            temps = (Double)_ruta.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;
        if (temps < _vehicle.carregaRestant()) {
            // Podem arribar, mirem si el candidat es acceptable
            switch (tipus) {
                case 'O':
                    acceptable = origenAcceptable(iCan);
                    break;
                case 'D':
                    //TODO: El desti es acceptable si tenim gent al cotxe encara que tinguem la bateria al 50% o menys ( afegim factor nou ? factor regular + factor critic ? )
                    acceptable = destiAcceptable(iCan, p);
                    break;
                case 'P':
                    acceptable = depotAcceptable(iCan);
                    break;
            }
        }
        //System.out.println(acceptable);
        return acceptable;
    }

    /**
     * @brief Ens diu si considerem el candidat origen com a acceptable, per
     * aixo s'han de cumplir dos condicions: 
     * ->Tenim prou espai per carregar els
     * clients que esperen. 
     * ->La bateria del vehicle es superior al 50%, sino entraria en contradiccio amb la primera llei robotica, que obliga al
     * automata a no agredir a cap huma.

     */
    private boolean origenAcceptable(CandidatRuta iCan) {
        double mitjaBat = _vehicle.carregaTotal() * FACTOR_CARREGA_CRITIC;
        return _solicituds.get(iCan.actual()/2).NumPassatgers() < (_vehicle.nPassTotal() -_vehicle.nPassatgers())
                && _vehicle.carregaRestant() > mitjaBat
                && _solicituds.get(iCan.actual()/2).getEstat() == Solicitud.ESTAT.ESPERA;
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
        //System.out.println("Tamany candidat: "  + _candidats.size() + "Tamany solicituds: " + _solicituds.size());
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
        //System.out.println("ANOTEM");
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _candidats.get(iCan.actual()).getValue();
        double temps;
        if (p != _ruta.lastElement())
            temps = _ruta.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;
        _ruta.push(_candidats.get(iCan.actual()).getValue());
        _vehicle.descarga(temps);
        _cost += temps;
        switch (tipus) {
            case 'O':
                _nPeticions++;
                _vehicle.ModificarPassatgers(_solicituds.get(iCan.actual() / 2).NumPassatgers());
                _solicituds.get(iCan.actual()/2).setEstat(Solicitud.ESTAT.ENTRANSIT);
                
                break;
            case 'D':
                _nPeticions--;
                _nPeticionsTramitades++;
                _vehicle.ModificarPassatgers(-1 * _solicituds.get(iCan.actual() / 2).NumPassatgers());
                _solicituds.get(iCan.actual()/2).setEstat(Solicitud.ESTAT.FINALITZADA);
                 
                break;
            case 'P':
                _vehicle.cargar(300);
                _cost += 300;
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
        //System.out.println("DESANOTEM");
        char tipus = _candidats.get(iCan.actual()).getKey();
        Node p = _ruta.pop();
         
        // System.out.println("ACTUAL: " + p + " ANTERIOR: " + _ruta.lastElement());
        double temps;
        if (p != _ruta.lastElement())
            temps = _ruta.lastElement().getEdgeBetween(p).getAttribute("pes");
        else
            temps = 0;
        _vehicle.cargar(temps);
        _cost -= temps;

        switch (tipus) {
            case 'O':
                _nPeticions--;
                _vehicle.ModificarPassatgers(-1*_solicituds.get(iCan.actual()/2).NumPassatgers());
                 _solicituds.get(iCan.actual()/2).setEstat(Solicitud.ESTAT.ESPERA);
                break;
            case 'D':
                _nPeticions++;
                _nPeticionsTramitades--;
                _vehicle.ModificarPassatgers(_solicituds.get(iCan.actual()/2).NumPassatgers());
                 _solicituds.get(iCan.actual()/2).setEstat(Solicitud.ESTAT.ENTRANSIT);
                break;
            case 'P':
                _vehicle.descarga(300);
                _cost -= 300;
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
        //System.out.println(_cost + "    ----    " + optim._cost);
        return _cost < optim._cost;
    }
    
    
    public Stack<Node> obtSolucio() {
        return _ruta;
    }
    
    public double obtCost() {
        return _cost;
    }
}
