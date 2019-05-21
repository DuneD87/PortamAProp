package PortamAProP;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import static java.time.temporal.ChronoUnit.MINUTES;
import org.graphstream.ui.view.Viewer;

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
    
    private StringBuilder _log;
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
     * @param log Fitxer que guarda informacio sobre l'execucio
     * @brief Completa la ruta
     * @pre S'ha cridat l'algoritme de backtracking previament
     * @post Completa la ruta amb les diferents dades obtenides per l'algoritme de backtracking
     */
    public void completarRuta(Stack<Node> n, Stack<Character> a,Stack<Integer> c, ArrayList<Peticio> s,LocalTime h,double tm, double tp,ArrayList<PeticioEnTramit> pComp, StringBuilder log) {
        _nodes = new ArrayList<>(n);
        _accions = new ArrayList<>(a);
        _carrega = new ArrayList<>(c);
        _solCompletades = new ArrayList<>(s);
        _horaFi = h;
        _tempsEnMarxa = tm;
        _tempsADepot = tp;
        _peticionsCompletades = pComp;
        _finalitzada = true;
        _log = log;
    }
    
    /**
     * @brief Mostra la ruta i detalls
     * @pre ---
     * @post S'ha mostrat la ruta..TO COMPLETE
     */
    public void mostrarRuta() {
        //_graf.display();
        _log.append("\n*****PETICIONS ATESES*****\n");
        for (PeticioEnTramit s : _peticionsCompletades) {
            _log.append(s.toString() + "\n");
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
        _log.append("RUTA DE NODES\n");
        Node q = _nodes.get(0);
        _log.append(q.getAttribute("Nom").toString());
        for(int i = 1; i < _nodes.size(); i++){
            _log.append("->"+_nodes.get(i).getAttribute("Nom"));
        }
        _log.append("\nHORA DE FINALITZACIO: " + _horaFi);
        _log.append("\nTEMPS TOTAL EN RUTA: " + _tempsEnMarxa + " minuts");
        _log.append("\nTEMPS A DEPOT: " + _tempsADepot + " minuts");
        _log.append("\nMITJANA DE PASSATGERS:" + mitjanaPassatgers() + " passatgers");
        _log.append("\nMITJANA DISTANCIA ENTRE NODES " + mitjanaDistanciaNodes() + " minuts");
        mitjanaTempsEsperaRecorregut();
        _log.append("\n");
       
    }
    /**
     * @brief Retorna el array de conversio
     * @post: Retorna _conversio
     */
    public int[] retornarConversio() {
        return _conversio;
    }
    /**
     * @brief Retorna la mitjana de passatgers de la ruta
     *        Per determinar la mitjana de passatgers de la ruta,
     *        suma tots els clients de les peticions i els divideix pel numero 
     *        de peticions
     * @post Retorna la mitjana de clients per peticio
     */
    public int mitjanaPassatgers(){
        int mitjana=0;
        for(Peticio s:_solicituds){
            mitjana+=s.numPassatgers();
        }
        mitjana=mitjana/_solicituds.size();
        _mitjanaPassatgers=mitjana;
        
        
        
        return mitjana;
    }
     /**
     * @brief Retorna la distancia promig entre els nodes del graf
     *        Per determinar la distancia promig entre els nodes del graf
     *        sumem tots els pessos de les aresetes del graf i ho dividim per
     *        per el numeo de arestes
     * @post Retorna la distancia promig entre nodes del graf
     */
    public double mitjanaDistanciaNodes(){
        double mitjana=0;
        double arestes=0;
        for(int i=0;i<_nodes.size()-1;i++){
            if(_nodes.get(i).getId().equals(_nodes.get(i+1).getId())){
                mitjana+=0;
                arestes++;
            }else{
                double pes=_graf.getNode(_nodes.get(i).getId()).getEdgeBetween(_nodes.get(i+1).getId()).getAttribute("pes");
                mitjana+=pes;
                arestes++;
            }
        }     
        _mitjanaDistanciaNodes=mitjana/arestes;
        return mitjana/_nodes.size();
    }
    
    /**
     * @breif Metode que detmina la mitjana de temps que el client esta esperant i la mitjana de temps que el client esta al vehicle
     * 
     *      Per determinar les mitjanes de temps, Per cada peticio:sumo els minuts entre temps de emisio de la peticio o el temps de recollida del client i les 23:59:59, i els minuts entre
     *      el temps de recollida de la petcio o el temps d'arribada del client i mitjanit(00:00) i despres li resto un dia sencer, finalment divideixo les sumes per el nombre de peticions
     */
    public void mitjanaTempsEsperaRecorregut(){
        long mitjanaEspera=0;
        long mitjanaRecorregut=0;
        for(PeticioEnTramit s: _peticionsCompletades){
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
                if(s.obtenirHoraRecollida().isBefore(casimit) && s.obtenirHoraArribada().isAfter(mitjanit)){
                    tempsRecorregut+=MINUTES.between(s.obtenirHoraRecollida(), casimit);
                    tempsRecorregut+=MINUTES.between(mitjanit, s.obtenirHoraArribada());
                    if(tempsRecorregut>180){
                        long diasencer=MINUTES.between(mitjanit, casimit);
                       tempsRecorregut-=(diasencer);
                       if(tempsRecorregut<0)
                           tempsRecorregut*=-1;
                    }
                }
                mitjanaRecorregut += tempsRecorregut;
           // }
        }
        long resultatMitjanaEspera=mitjanaEspera/_peticionsCompletades.size();
        long resultatMitjanaRecorrecut=mitjanaRecorregut/_peticionsCompletades.size();
        _mitjanaEsperaClient=resultatMitjanaEspera;
        _mitjanaMarxaClient=resultatMitjanaRecorrecut;
        _log.append("\nMITJANA D'ESPERA DE CLIENT:" + resultatMitjanaEspera + " minuts");
        _log.append("\nMITJANA DE RECORRECUT DE CLIENT: " + resultatMitjanaRecorrecut + " minuts");
        
       
    }
    
    
  /**
   * @brief Retrona el temps d'emisio de la primera peticio
   * @post Retorna el temps d'emisio de la primera solicitud
   */
    public LocalTime obtHoraPrimeraPeticio() {
        return _solicituds.first().emissio();
    }
    /**
   * @brief Retrona el temps en marxa del vehicle a la ruta
   * @post Retorna _tempsEnMarxa
   */
    public double tempsEnMarxa(){
        return _tempsEnMarxa;
    }
      /**
   * @brief Retrona el temps de carga del vehicle a la ruta
   * @post Retorna _tempsADepot
   */
    public double tempsDepot(){
        return _tempsADepot;
    }
      /**
   * @brief Ens diu si la ruta ha estat finalitzada o no
   * @post Retorna _finalitzada
   */
    public boolean finalitzada(){
        return _finalitzada;
    }
    
      /**
   * @brief Ens diu la distancia promig entre entre els nodes del graf
   * @post Retorna _mitjanaDistanciaNodes
   */
    public double gmitjanaDistanciaNodes(){
        return _mitjanaDistanciaNodes;
    }

    /**
     * @brief Ens diu la mitjana de passatgers de les peticions de la ruta
     * @post Retorna _mitjanaPassatgers
     */
    public double gmitjanaPassatgers(){
        return _mitjanaPassatgers;
    }
    /**
     * @brief Ens diu la mitjana de temps d'espera del client 
     * @post Retorna _mitjanaEsperaClient
     */
    public double gmitjanaTempsEsperaClient(){
        return _mitjanaEsperaClient;
    }
    /**
     * @brief Ens diu la mitjana de temps que el client esta al vehicle
     * @post Retorna _mitjanaMarxaClient
     */
    public double gmitjanaTempsMarxaClient(){
        return _mitjanaMarxaClient;
    }
    /**
     * @brief Metode que marca la ruta del vehicle en el graf complet
     * 
     *      Per Marcar la ruta en el graf, les arestes i nodes que partanyen a la ruta 
     *      els posem un atribut marked que modifica el especte determinat al styleSheet
     * @post Modifica el graf per poder mostrar la ruta
     */

    public void mostrarRutaSugraf() {
        int contador=0;
        for (int i = 0; i < _nodes.size() - 1; i++) {
            if (!_nodes.get(i).getId().equals(_nodes.get(i + 1).getId())) {
                _graf.getNode(_nodes.get(i).getId()).getEdgeBetween(_nodes.get(i + 1).getId()).setAttribute("ui.class", "marked");
                if (contador == 0) {
                        _graf.getNode(_nodes.get(i).getId()).setAttribute("ui.class", "principi");
                        _graf.getNode(_nodes.get(i + 1).getId()).setAttribute("ui.class", "marked");
                         contador++;
                } else {
                    _graf.getNode(_nodes.get(i).getId()).setAttribute("ui.class", "marked");
                    _graf.getNode(_nodes.get(i + 1).getId()).setAttribute("ui.class", "fi");
                }
            }
           
        }
        
        Viewer display = _graf.display(true);
        display.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }
    
    /**
     * @brief Fitxa de configuracio de la apariencia del graf
     */
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
        "}"+
            "node.principi{"
            + "fill-color: green;"
            + "size:15;"
            + "}"
            + "node.fi{"
            + "fill-color: yellow;"
            + "size:15;"
            + "}";
    
  
}
