package PortamAProP;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.util.Pair;
import org.graphstream.graph.*;

public class Ruta {
    
    private Vehicle _vehicle;//@brief Vehicle que fa la ruta
    private TreeSet<Solicitud> _solicituds;//@brief Conjunt solicituds ordenades decreixement per data, 
    private Graph _graf;//@brief Subgraph per on es moura el nostre vehicle
    private Stack<Node> _ruta;//@brief Solucio obtenida per l'algoritme de backtracking
    private int [] _conversio; // @brief array de pairs que assosia l'index dels nodes del subgraf de la ruta amb l'index del graf complet
    
    /**
     * @brief Constructor
     * @post Construim una nova ruta fen servir un vehicle, un conjunt de solicituds i un subgraph
     */
    public Ruta(Vehicle vehicle,TreeSet<Solicitud> sol,Graph g, int [] c ){
        _vehicle=vehicle;
        _solicituds=sol;
        _graf=g;
        _ruta = new Stack<>();
        _conversio= c;
    }
    
    /**
     * @return Cadena de caracters que ens dona informacio sobre la ruta (quin vehicle la fa i les solicituds acceptades) 
     */
    @Override
    public String toString(){
    String ruta="Vehicle de la ruta:\n"+ _vehicle.toString() + 
            "Solicituds de la ruta: \n" + _solicituds.toString();
    return ruta;
    }
    
    /**
     * @post Mostra el graf
     */
    public void MostrarGraf(){
        _graf.display();
    }
    
    /**
     * @post Ens dona el vehicle que fa la ruta
     */
    public Vehicle getVehicle() {
        return _vehicle;
    }
    
    /**
     * @post Conjunt de solicituds
     */
    public ArrayList<Solicitud> getSol() {
       return new ArrayList<>(_solicituds);
    }
    /**
     * @post Ens dona els nodes
     * @return 
     */
    public ArrayList<Node> getNodes() {
        return new ArrayList<>(_graf.getNodeSet());
    }
    
    /**
     * @post Ens dona el graf
     * @return 
     */
    public Graph getGraph() {
        return _graf;
    }
    
    /**
     * @brief Anota un node a la ruta
     * @pre Node ha de formar part del conjunt
     * @post S'ha empilat un node al cim de la pila
     */
    public void afegirNode(Node p) {
        _ruta.add(p);
    }
    
    /**
     * @brief Desanota un node a la ruta
     * @pre Pila no buida
     * @post S'ha desempilat l'ultim node al cim de la pila
     */
    public void desanotarNode() {
        _ruta.pop();
    }
    
    public int [] retornarConversio(){
        return _conversio;
    }
}
