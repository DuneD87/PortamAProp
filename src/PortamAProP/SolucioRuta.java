package PortamAProP;

import com.sun.jmx.remote.internal.ArrayQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import org.graphstream.graph.Node;

/**
 * @class SolucioRuta
 * @brief Classe encarregada de guardar les diferents solucions
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class SolucioRuta {
    
    private List<Node> _solucioActual; //@brief Solucio al algoritme, llista de nodes ordenats que recorre el nostre vehicle
    private List<Node> _nodes; //@brief Llista de nodes inicial
    private SortedSet<Solicitud> _solicituds; //@brief Llista de solicituds ordenades per hora de emissio
    private int _nivell; //@brief Nivell recursiu en el que es troba l'algoritme
    private Vehicle _cotxe; //@brief Vehicle encarregat d atendre les solicituds 
    
    /**
     * @brief Constructor
     * @param nodes Vector de nodes
     * @param solicituds SortedSet de solicituds
     */
    public SolucioRuta(List<Node> nodes, SortedSet<Solicitud> solicituds, Vehicle cotxe) {
        _solucioActual = new ArrayList<>();
        _nodes = nodes;
        _solicituds = solicituds;
        _cotxe = cotxe;
    }
    
    /**
     * @brief Inicialitza el candidat
     * @pre ---
     * @post S'ha inicicialitzat el candidat amb el maxim de nodes
     */
    public CandidatRuta iniCan() {
        return new CandidatRuta(_nodes.size());
    }
    
    /**
     * @brief Solucio acceptable
     * @pre ---
     * @post Ens diu si el candidat es acceptable
     */
    public boolean acceptable(CandidatRuta iCan) {
        
        return true;
    }
    
    /**
     * @brief Ens diu si la solucio es completa
     * @pre ---
     * @post Boolea que ens diu si el nivell ha arribat al nombre de candidats
     */
    public boolean completa(CandidatRuta iCan) {
        return _nivell == iCan.actual();
    }
    
    /**
     * @brief Anota el candidat
     * @pre ---
     * @post Guarda el node actual dins d'una llista de nodes
     */
    public void anotar(CandidatRuta iCan) {
        
    }
    
    /**
     * @brief Desanota el candidat
     * @pre ---
     * @post Borra el node actual de la llista de nodes
     */
    public void desanotar() {
        
    }
    
}
