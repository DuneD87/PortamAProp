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
    private ArrayList<Solicitud> _solicituds; //@brief Llista de solicituds ordenades per hora de emissio
    private int _nivell; //@brief Nivell recursiu en el que es troba l'algoritme
    private Vehicle _vehicle; //@brief Vehicle encarregat d atendre les solicituds 
    private boolean _enTransit;
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
            if (_vehicle.getPosicio() == _solicituds.get(_nivell).Origen() && !_enTransit) {
                //Pujen passatgers i ens moguem cap el desti
                _enTransit = true;
                _vehicle.ModificarPassatgers(_nivell); 
            }
            acceptable = true;
        }
        return acceptable;
    }
    
    /**
     * @brief Solucio definitiva
     * @pre ---
     * @post Ens diu si totes les solicituds s'han completat
     */
    public boolean definitiva() {
        return _nivell == _solicituds.size();
    }
    
    /**
     * @brief Solicitud completa
     * @pre ---
     * @post Ens diu si s'ha completat la solicitud, el vehicle ha arribat al desti amb els passatgers
     */
    public boolean completa(CandidatRuta iCan) {
        boolean esCompleta = _vehicle.getPosicio() == _solicituds.get(_nivell).Desti() && _enTransit;
        if (esCompleta) {
            _vehicle.ModificarPassatgers(-1*_solicituds.get(_nivell).NumPassatgers());//Es baixen els passatgers
            _nivell++;//Seguent solicitud
            iniCan();//Inicialitzem de nou candidats
            _enTransit = false;// el cotxe no porta passatgers ja
        }
        return esCompleta;
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
