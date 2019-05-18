package PortamAProP;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import static java.time.temporal.ChronoUnit.MINUTES;
import javafx.util.converter.LocalTimeStringConverter;

public class Ruta {

    private Vehicle _vehicle;//@brief Vehicle que fa la ruta
    private TreeSet<Peticio> _solicituds;//@brief Conjunt peticions ordenades decreixement per data, 
    private Graph _graf;//@brief Subgraph per on es moura el nostre vehicle
    private Stack<Node> _ruta;//@brief Solucio obtenida per l'algoritme de backtracking
    private int[] _conversio; // @brief array de pairs que assosia l'index dels nodes del subgraf de la ruta amb l'index del graf complet
    
    private ArrayList<Peticio> _solCompletades;//@brief Llistat de les peticions completades
    private ArrayList<Node> _nodes;//@brief Llistat de nodes que descriuen la ruta efectuada
    private ArrayList<Character> _accions;//@brief Llistat d'accions que s'ha efectuat a cada node
    private ArrayList<Integer> _carrega;//@brief Llistat de persones que han baixat o pujat en peticions, o temps a depot
    private boolean _finalitzada;//@brief Ens diu si el bactracking ha trobat una solucio a la ruta
    private ArrayList<PeticioEnTramit> _peticionsCompletades;//@brief Llista de les peticions completades
    
    private double _tempsEnMarxa;//@brief Temps que el vehicle esta en marxa
    private double _tempsADepot;//@brief Temps que el vehicle esta a depot
    
    private LocalTime _horaFi;//@brief Hora on finalitza la ruta
    private LocalTime _horaInici;//@brief Hora en que iniciem la ruta

    
    private int _mitjanaPassatgers;
    private double _mitjanaDistanciaNodes;
    private double _mitjanaEsperaClient;
    private double _mitjanaMarxaClient;
    /**
     * @brief Constructor
     * @post Construim una nova ruta fen servir un vehicle, un conjunt de
     * peticions i un subgraph
     */
    public Ruta(Vehicle vehicle, TreeSet<Peticio> sol, Graph g, int[] c) {
        _vehicle = vehicle;
        _solicituds = sol;
        _graf = g;
        _conversio = c;
        _finalitzada = false;
        _peticionsCompletades = new ArrayList<>();
        _mitjanaDistanciaNodes=0;
        _mitjanaEsperaClient=0;
        _mitjanaMarxaClient=0;
        _mitjanaPassatgers=0;
        _graf.addAttribute("ui.stylesheet", styleSheet);
        
    }

    /**
     * @brief toString metode
     * @return Cadena de caracters que ens dona informacio sobre la ruta (quin
     * vehicle la fa i les peticions acceptades)
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
     * @post Conjunt de peticions
     */
    public ArrayList<Peticio> getSol() {
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
     * @param s Llista de peticions completades
     * @param h Hora de finalitzacio de la ruta
     * @param tm Temps en marxa del vehicle
     * @param tp Temps a depot
     * @param pComp Llistat de peticions tramitades
     * @brief Completa la ruta
     * @pre S'ha cridat l'algoritme de backtracking previament
     * @post Completa la ruta amb les diferents dades obtenides per l'algoritme de backtracking
     */
    public void completarRuta(Stack<Node> n, Stack<Character> a,Stack<Integer> c, ArrayList<Peticio> s,LocalTime h,double tm, double tp,ArrayList<PeticioEnTramit> pComp) {
        _nodes = new ArrayList<>(n);
        _accions = new ArrayList<>(a);
        _carrega = new ArrayList<>(c);
        _solCompletades = new ArrayList<>(s);
        _horaFi = h;
        _tempsEnMarxa = tm;
        _tempsADepot = tp;
        _peticionsCompletades = pComp;
        _finalitzada = true;
    }
    
    /**
     * @brief Mostra la ruta i detalls
     * @pre ---
     * @post S'ha mostrat la ruta..TO COMPLETE
     */
    public void mostrarRuta() {
        _graf.display();
        System.out.println("*****PETICIONS ATESES*****");
        for (PeticioEnTramit s : _peticionsCompletades) {
            System.out.println(s.toString());
        }
        Graph g = new MultiGraph("Sol");
        for (int i = 0; i < _nodes.size(); i++) {
            if(g.getNode(_nodes.get(i).getId())==null){
                g.addNode(_nodes.get(i).getId());
                g.getNode(_nodes.get(i).getId()).setAttribute("ui.label", _nodes.get(i).getId());
            }
        }int numaresta=1;
        for (int i = 0; i < _nodes.size() - 1; i++) {
            if (! _nodes.get(i).getId().equals(_nodes.get(i + 1).getId())) {
                g.addEdge(Integer.toString(i), _nodes.get(i).getId(), _nodes.get(i + 1).getId(),true);
                g.getEdge(Integer.toString(i)).setAttribute("ui.label", Integer.toString(numaresta));
                numaresta++;
            }
        }
        //g.display();
        System.out.println("RUTA DE NODES");
        for(Node n:_nodes){
            System.out.print("-"+n.getId());
        }
        System.out.println("\nHORA DE FINALITZACIO: " + _horaFi);
        System.out.println("TEMPS TOTAL EN RUTA: " + _tempsEnMarxa);
        System.out.println("TEMPS A DEPOT: " + _tempsADepot);
        System.out.println("MITJANA DE PASSATGERS:" + mitjanaPassatgers());
        System.out.println("MITJANA DISTANCIA ENTRE NODES " + mitjanaDistanciaNodes());
        mitjanaTempsEsperaRecorregut();
        System.out.println("\n");
       
    }

    public int[] retornarConversio() {
        return _conversio;
    }
    public int mitjanaPassatgers(){
        int mitjana=0;
        for(Peticio s:_solicituds){
            mitjana+=s.numPassatgers();
        }
        mitjana=mitjana/_solicituds.size();
        _mitjanaPassatgers=mitjana;
        
        
        
        return mitjana;
    }
    public double mitjanaDistanciaNodes(){
        double mitjana=0;
        for(int i=0;i<_nodes.size()-1;i++){
            if(_nodes.get(i).getId().equals(_nodes.get(i+1).getId()))
                mitjana+=0;
            else{
                double pes=_graf.getNode(_nodes.get(i).getId()).getEdgeBetween(_nodes.get(i+1).getId()).getAttribute("pes");
                mitjana+=pes;
            }
        }     
        _mitjanaDistanciaNodes=mitjana/_nodes.size();
        return mitjana/_nodes.size();
    }
    
    public void mitjanaTempsEsperaRecorregut(){
        long mitjanaEspera=0;
        long mitjanaRecorregut=0;
        for(PeticioEnTramit s: _peticionsCompletades){
            //if (s.getRecollida() != null) {
               // System.out.println("Hora Emisio " + s.horaEmissio());
                //System.out.println("Hora Recollida " + s.obtenirHoraRecollida());
                //System.out.println("Hora Arribada " + s.obtenirHoraArribada());
                LocalTime mitjanit=LocalTime.of(0, 0);
                LocalTime casimit=LocalTime.of(23, 59,59);
                long tempsEspera=0;
                if(s.horaEmissio().isBefore(casimit) && s.obtenirHoraRecollida().isAfter(mitjanit)){
                    tempsEspera+=MINUTES.between(s.horaEmissio(), casimit);
                    tempsEspera+=MINUTES.between(mitjanit, s.obtenirHoraRecollida());
                    if(tempsEspera>180){
                         long diasencer=MINUTES.between(mitjanit, casimit);
                         tempsEspera-=(diasencer);
                         if(tempsEspera<0){
                             tempsEspera*=-1;
                         }
                    }
                }
                
                mitjanaEspera += tempsEspera;
                long tempsRecorregut=0;
                 //System.out.println(mitjanit);
                 //System.out.println(s.obtenirHoraRecollida());
                 //System.out.println(s.obtenirHoraArribada());
                 //System.out.println(s.obtenirHoraRecollida().isBefore(casimit));
                 //System.out.println(s.obtenirHoraArribada().isAfter(mitjanit));
                if(s.obtenirHoraRecollida().isBefore(casimit) && s.obtenirHoraArribada().isAfter(mitjanit)){
                    tempsRecorregut+=MINUTES.between(s.obtenirHoraRecollida(), casimit);
                    //System.out.println("Temps1 " + tempsRecorregut);
                    tempsRecorregut+=MINUTES.between(mitjanit, s.obtenirHoraArribada());
                    // System.out.println(MINUTES.between(mitjanit, s.obtenirHoraArribada()));
                    //System.out.println("Temps2 " + tempsRecorregut);
                    if(tempsRecorregut>180){
                        long diasencer=MINUTES.between(mitjanit, casimit);
                       tempsRecorregut-=(diasencer);
                       if(tempsRecorregut<0)
                           tempsRecorregut*=-1;
                        //System.out.println(diasencer);
                         //System.out.println("Temps3" + tempsRecorregut);
                    }
                }
                //System.out.println("Temps" + tempsRecorregut);
                mitjanaRecorregut += tempsRecorregut;
           // }
        }
        long resultatMitjanaEspera=mitjanaEspera/_peticionsCompletades.size();
        long resultatMitjanaRecorrecut=mitjanaRecorregut/_peticionsCompletades.size();
        _mitjanaEsperaClient=resultatMitjanaEspera;
        _mitjanaMarxaClient=resultatMitjanaRecorrecut;
        System.out.println("MITJANA D'ESPERA DE CLIENT:" + resultatMitjanaEspera);
        System.out.println("MITJANA DE RECORRECUT DE CLIENT: " + resultatMitjanaRecorrecut);
        
       
    }
    
    
  
    public LocalTime obtHoraPrimeraPeticio() {
        return _solicituds.first().emissio();
    }
    
    public double tempsEnMarxa(){
        return _tempsEnMarxa;
    }
    
    public double tempsDepot(){
        return _tempsADepot;
    }
    
    public boolean finalitzada(){
        return _finalitzada;
    }
    
    public double gmitjanaDistanciaNodes(){
        return _mitjanaDistanciaNodes;
    }
    
    public double gmitjanaPassatgers(){
        return _mitjanaPassatgers;
    }
    
    public double gmitjanaTempsEsperaClient(){
        return _mitjanaEsperaClient;
    }
    
    public double gmitjanaTempsMarxaClient(){
        return _mitjanaMarxaClient;
    }
    
    public void mostrarRutaSugraf(){
        for (int i = 0; i < _nodes.size() - 1; i++) {
            if (! _nodes.get(i).getId().equals(_nodes.get(i + 1).getId())) {
                 _graf.getNode(i).getEdgeBetween(i+1).setAttribute("ui.class", "marked");
                 _graf.getNode(i).setAttribute("ui.class", "marked");
                 _graf.getNode(i+1).setAttribute("ui.class", "marked");
            }
        }
        _graf.display();
    }
    
    protected String styleSheet= 
         "edge{" +
        "	fill-color: black;" +
        "}" +
        "edge.marked {" +
        "	fill-color: red;"
            + " size: 5;" +
        "}" +  "node{" +
        "	fill-color: black;" +
        "}" +
        "node.marked {" +
        "	fill-color: blue;"
            + "size: 15;" +
        "}";
    
  
}
