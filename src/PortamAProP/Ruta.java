package PortamAProP;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;

public class Ruta {

    private Vehicle _vehicle;//@brief Vehicle que fa la ruta
    private TreeSet<Solicitud> _solicituds;//@brief Conjunt solicituds ordenades decreixement per data, 
    private Graph _graf;//@brief Subgraph per on es moura el nostre vehicle
    private Stack<Node> _ruta;//@brief Solucio obtenida per l'algoritme de backtracking
    private int[] _conversio; // @brief array de pairs que assosia l'index dels nodes del subgraf de la ruta amb l'index del graf complet
    
    private ArrayList<Solicitud> _solCompletades;//@brief Llistat de les solicituds completades
    private ArrayList<Node> _nodes;//@brief Llistat de nodes que descriuen la ruta efectuada
    private ArrayList<Character> _accions;//@brief Llistat d'accions que s'ha efectuat a cada node
    private ArrayList<Integer> _carrega;//@brief Llistat de persones que han baixat o pujat en solicituds, o temps a depot
    
    private double _tempsEnMarxa;//@brief Temps que el vehicle esta en marxa
    private double _tempsADepot;//@brief Temps que el vehicle esta a depot
    
    private LocalTime _horaFi;//@brief Hora on finalitza la ruta
    private LocalTime _horaInici;//@brief Hora en que iniciem la ruta

    /**
     * @brief Constructor
     * @post Construim una nova ruta fen servir un vehicle, un conjunt de
     * solicituds i un subgraph
     */
    public Ruta(Vehicle vehicle, TreeSet<Solicitud> sol, Graph g, int[] c) {
        _vehicle = vehicle;
        _solicituds = sol;
        _graf = g;
        _conversio = c;
    }

    /**
     * @return Cadena de caracters que ens dona informacio sobre la ruta (quin
     * vehicle la fa i les solicituds acceptades)
     */
    @Override
    public String toString() {
        String ruta = "Vehicle de la ruta:\n" + _vehicle.toString()
                + "\nSolicituds de la ruta: \n" + _solicituds.toString();
        return ruta;
    }

    /**
     * @post Mostra el graf
     */
    public void MostrarGraf() {
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
     * @param n Pila de nodes que descriu la ruta del vehicle
     * @param a Pila d'accions que s'han efecuat a cada node
     * @param c Pila de persones que han baixat/pujat a cada node o temps carregan
     * @param s Llista de solicituds completades
     * @param h Hora de finalitzacio de la ruta
     * @param tm Temps en marxa del vehicle
     * @param tp Temps a depot
     * @brief Completa la ruta
     * @pre S'ha cridat l'algoritme de backtracking previament
     * @post Completa la ruta amb les diferents dades obtenides per l'algoritme de backtracking
     */
    public void completarRuta(Stack<Node> n, Stack<Character> a,Stack<Integer> c, ArrayList<Solicitud> s,LocalTime h,double tm, double tp) {
        _nodes = new ArrayList<>(n);
        _accions = new ArrayList<>(a);
        _carrega = new ArrayList<>(c);
        _solCompletades = s;
        _horaFi = h;
        _tempsEnMarxa = tm;
        _tempsADepot = tp;
    }
    
    /**
     * @brief Mostra la ruta i detalls
     * @pre ---
     * @post S'ha mostrat la ruta..TO COMPLETE
     * @return 
     */
    public void mostrarRuta() {
        System.out.println("*****SOLICITUDS ATESES*****");
        for (Solicitud s : _solCompletades)
            s.toString();
        Graph g = new DefaultGraph("Sol");
        for (int i = 0; i < _nodes.size(); i++) {
            Node n = null;
            if (g.getNode(_nodes.get(i).getId()) == null)
                n = g.addNode(_nodes.get(i).getId());
            /*n.addAttribute("Tipus", _nodes.get(i).getAttribute("Tipus"));
            n.addAttribute("Nom", _nodes.get(i).getAttribute("Nom"));*/
            if (i < _nodes.size() - 1) {
                if (n != null)
                    g.addEdge("Edge" + i, n, _nodes.get(i + 1));
            }
        }
        g.display();
        System.out.println("HORA DE FINALITZACIO: " + _horaFi);
        System.out.println("TEMPS TOTAL EN RUTA: " + _tempsEnMarxa);
        System.out.println("TEMPS A DEPOT: " + _tempsADepot);
    }

    public int[] retornarConversio() {
        return _conversio;
    }
}
