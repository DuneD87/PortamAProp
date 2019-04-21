package PortamAProP;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.Stack;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * @class SolucioRuta
 * @brief Classe encarregada de guardar les diferents solucions
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class SolucioRuta {
    
    private Stack<Integer> _solucioActual; //@brief Solucio al algoritme, llista de nodes ordenats que recorre el nostre vehicle
    private Graph _graf; //@brief Llista de nodes inicial
    private ArrayList<Node> _depots;
    private ArrayList<Solicitud> _solicituds; //@brief Llista de solicituds ordenades per hora de emissio
    private int _nivell; //@brief Nivell recursiu en el que es troba l'algoritme
    private Vehicle _vehicle; //@brief Vehicle encarregat d atendre les solicituds 
    private boolean _enTransit; //@brief Ens diu si el vehicle ja ha recollit els passatgers
    
    /**
     * @brief Constructor
     * @param nodes Vector de nodes
     * @param solicituds SortedSet de solicituds
     */
    public SolucioRuta(Graph graf, ArrayList<Solicitud> solicituds, Vehicle cotxe) {
        _solucioActual = new Stack<>();
        _graf = graf;
        _solicituds = solicituds;
        _vehicle = cotxe;
        _enTransit = false;
        for (Node n : _graf.getNodeSet()) 
            if (n.getAttribute("Tipus") == "Depot")
                _depots.add(n);
    }
    
    /**
     * @brief Inicialitza el candidat
     * @pre ---
     * @post S'ha inicicialitzat el candidat amb el maxim de nodes
     */
    public CandidatRuta iniCan() {
        return new CandidatRuta(_vehicle.getPosicio(),_graf.getNodeCount());
    }
    
    /**
     * @brief Solucio acceptable
     * @pre ---
     * @post Ens diu si el candidat es acceptable
     */
    public boolean acceptable(CandidatRuta iCan) {
        boolean acceptable = false;
        double carregaPrevista = _graf.getNode(_vehicle.getPosicio()).getEdgeBetween(iCan.actual()).getAttribute("Pes");
        //Primer preguntem si el cotxe pot arribar al node
        if (carregaPrevista < _vehicle.carregaRestant()) {
            //Recollim passatgers ?
            String tipus = _graf.getNode(iCan.actual()).getAttribute("Tipus");
            if (tipus == "Solicitud") {//Ens trobem amb una solicitud
                //Tenim lloc al vehicle ?
                int llocsRestants = _vehicle.nPassatgers() - _vehicle.nPassTotal();
                if (llocsRestants <= _solicituds.get(_nivell).NumPassatgers()) {
                    carregarPassatgers();
                    acceptable = true;
                }
            }
        } else { //No tenim prou fuel, anem al depot mes proper i carreguem duran 15min
            despDepotProper();
        }
        return acceptable;
    }
    /**
     * @brief Desplaça el vehicle al depot mes proper
     * @pre Vehicle amb carrega superior al pes entre la posicio del vehicle i el depot mes proper
     * @post S'ha desplaçat el vehicle al depot mes proper
     */
    private void despDepotProper() {
        double distMin = Double.MAX_VALUE; //Confiem que no hi hagi cap pes del graf amb aquet valor :P
        int id;
        for (Node n : _depots) {
            double distAct = (Double)_graf.getNode(_vehicle.getPosicio()).getEdgeBetween(n).getAttribute("Pes");
            if ( distAct <= distMin) {
                distMin = distAct;
                id = n.getIndex();
            }
        }
    }
    
    /**
     * @brief Carrega passatgers
     * @pre ---
     * @post S'ha modificat el numero de passatgers del vehicle
     */
    private void carregarPassatgers() {
        _vehicle.ModificarPassatgers(_solicituds.get(_nivell).NumPassatgers());
    }
    
    /**
     * @brief Ens diu si ha finalitzat la solicitud
     * @pre ---
     * @post Ens diu si ha finalitzat la solicitud actual
     */
    private boolean haFinalitzat(CandidatRuta iCan) {
        int id = _solicituds.get(_nivell).Desti();
        return _solicituds.get(_nivell).getEstat() == Solicitud.ESTAT.ENTRANSIT && id == iCan.actual();
    }
    
    /**
     * @brief Solicitud completa
     * @pre ---
     * @post Ens diu si s'ha completat la solicitud, el vehicle ha arribat al desti amb els passatgers
     */
    public boolean completa(CandidatRuta iCan) {
       return _nivell == _solicituds.size();
    }
    
    /**
     * @brief Anota el candidat
     * @pre ---
     * @post Guarda el node actual dins d'una llista de nodes
     */
    public void anotar(CandidatRuta iCan) {
        //Anotem el nou node
        _solucioActual.push(iCan.actual());
        //restem el pes entre la posicio actual del vehicle i el candidat
        _vehicle.descarga(_graf.getNode(_vehicle.getPosicio()).getEdgeBetween(iCan.actual()).getAttribute("Pes"));
        //Actualitzem la posicio del vehicle a iCan
        _vehicle.setPosicio(iCan.actual());
    }
    
    /**
     * @brief Desanota el candidat
     * @pre ---
     * @post Borra el node actual de la llista de nodes
     */
    public void desanotar() {
        //Desanotem el node
        int nodeUltim = _solucioActual.pop(); //L'ultima solucio valida, tornem enrera
        //Carreguem el vehicle
        _vehicle.cargar(_graf.getNode(nodeUltim).getEdgeBetween(_vehicle.getPosicio()).getAttribute("Pes"));
        //Tornem enrera
        _vehicle.setPosicio(nodeUltim);
    }
    
}
