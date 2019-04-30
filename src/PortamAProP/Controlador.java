package PortamAProP;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javafx.util.Pair;
import javax.swing.text.StyledEditorKit;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import scala.Int;

/**
 * @brief Classe controladora, es dedica a gestionar les diferents opcions del
 * programa
 * @input Menu provisional per fer diferents execucions
 * @output ...
 * @author drive
 */
public class Controlador {

    private Graph _graf; // @brief Ens guardem el graf com atribut per executar els diferents algoritmes sobre ell
    private SortedSet<Solicitud> _solicituds; // @brief Vector de solicituds, el fem servir en els diferents algoritmes
    private List<Vehicle> _vehicles;//@brief Estructura on ens guardem els vehicles
    private GeneradorNodesGraf _generadorNodes; // @brief Objecte que ens permet generar un conjunt de nodes aleatoriament
    private GeneradorSolicituds _generadorSol; // @brief Objecte que ens permet generar un conjunt de solicituds aleatoriament
    private String NOM_FITXER_D = "Depots.txt";
    private String NOM_FITXER_G = "Graf.txt";
    private String FORMAT_ENTRADA_GRAF="R";
    private List<Pair<Vehicle,TreeSet<Solicitud>>> _ruta = new ArrayList<Pair<Vehicle,TreeSet<Solicitud>>>(10);
    private LlegirFitxerGraf mapa;
    private Object[] _nodes;
    private Object[] _arestes;
    private int MAX_DISTANCIA_GREEDY=100;//@brief distancia maxima acceptada pel greedy
    private ArrayList<Ruta> _rutes;
    /**
     * @brief Constructor per defecte
     * @pre ---
     * @post S'ha construit un objecte controlador per defecte
     */
    public Controlador() {
        _graf = new SingleGraph("MAPA");
        generarGraf();
        _solicituds = new TreeSet<>();
        generarSolicituds();
        _vehicles = new ArrayList<>();
        generarVehicles();
        _ruta= new ArrayList<Pair<Vehicle,TreeSet<Solicitud>>>(10);
        _rutes=new ArrayList<Ruta>();
    }

    /**
     * @brief Inicialitza el nostre programa
     * @pre ---
     * @post S'ha inicialitzat el programa
     */
    public void init() {
        mostrarMenu();
        gestionarMenu();
    }
    
    /**
     * @brief Gestiona les diferents opcions del menu
     * @pre ---
     * @post S'han executat les diferents comandes (provisional)
     */
    public void gestionarMenu() {

        Scanner inText = new Scanner(System.in);
        System.out.println("Comanda:");
        int opcio = Integer.parseInt(inText.nextLine());
        while (opcio != 0) {
            switch (opcio) {
                case 1:
                    _graf.display(true); 
                    
                   // for(Node n: _graf){
                     //   System.out.println("Id: " + n.getId() + " Vehicles actuals: " + n.getAttribute("VehicleActual"));
                    //}
                    for(int i=0;i<_graf.getNodeCount();i++){
                        System.out.println("Id: " + _graf.getNode(i).getId() + " Vehicles actuals: " + _graf.getNode(i).getAttribute("VehiclesActual"));
                    }
                    break;
                case 2:
                    algoritmeBacktracking();
                    break;
                case 3:
                    
                    break;
                case 4:
                    _graf.display();
                    AssignarSolicitudsAVehicles();
                    //MostrarVehiclesSolicituds();
                    //MostrarSolicitudsNoAssignades();
                    break;
                case 5:
                    MostrarSolicitudsNoAssignades();
                    break;
                case 6: 
                    MostrarRutes();
                    break;
            }
            System.out.println("Comanda:");
            opcio = Integer.parseInt(inText.nextLine());
        }
    }

    /**
     * @brief Mostra el menu
     * @pre ---
     * @post Menu provisional
     */
    public void mostrarMenu() {
        System.out.println("MENU\n"
                + "1 - Mostrar Graf\n"
                + "2 - Algoritme Backtracking\n"
                + "0 - Sortir");
    }

    /**
     * @brief Inicialitza els vehicles
     * @pre ---
     * @post S'han incialitzat els vehicles i guardats en una estructura de
     * dades
     */
    private void generarVehicles() {
        LlegirFitxersVehicle lVehicle = new LlegirFitxersVehicle();
        _vehicles = lVehicle.obtVehicles();
        
        for(int i=0;i<_vehicles.size();i++){
            Vehicle v=_vehicles.get(i);
            System.out.println(v.nodeInicial());
            Integer n=Integer.parseInt(_graf.getNode(v.nodeInicial()).getAttribute("VehiclesActual"))+1;
            String s=n.toString();
            _graf.getNode(v.nodeInicial()).setAttribute("VehiclesActual", s);
        }  
    }

    /**
     * @brief Inicialitza les solicituds
     * @pre ---
     * @post S'han inicialitzats les solicituds
     */
    private void generarSolicituds() {
        GeneradorSolicituds sol = new GeneradorSolicituds();
        String lSol = sol.toString();
        System.out.println(lSol);
        LlegirFitxerSolicitud lFitxer = new LlegirFitxerSolicitud(lSol, _graf);
        _solicituds = lFitxer.obtSol();
    }

    /**
     * @brief Crea el graf
     * @pre ---
     * @post S'ha creat el graf
     */
    private void generarGraf() {

        mapa = new LlegirFitxerGraf();
        mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_D, "Depot");//Els Depots sempre es Generer primer i a partir de un fitxer
        System.out.println("Com vols generar la resta del graf? Random o Fitxer [R/F]:");
        //Scanner teclat=new Scanner(System.in);
        //String opcio=teclat.nextLine();
        String opcio=FORMAT_ENTRADA_GRAF;
        
        if(opcio.equals("R")){
            _generadorNodes = new GeneradorNodesGraf();
            _generadorNodes.GeneradorAleatoriNodes(_graf.getNodeCount());
            String nodes=_generadorNodes.OptenirNodes();
            mapa.ModificarGrafPerString(_graf, nodes,"Solicitud");    
        }else if(opcio.equals("F")){
            mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_G, "Solicitud");
        }

        _graf = mapa.obtGraph();
        System.out.println("Nodes inserits correctament");
        
       _nodes=RetornarArrayNodes(_graf);
       _arestes=RetornarArrayArestes(_graf);

    }

     /**
     * @brief Assigna per cada vehicle, un grup de solicituds, tambe assigna tots els depots
     * @pre ---
     * @post Afageix a _ruta, vehicles amb unes solicituds 
     */
    public void AssignarSolicitudsAVehicles() {
        
            //for (int i = 0; i < _vehicles.size(); i++) 
            int i=0;
            int y=0;
            int anterior=NumeroSolicitudsNoAssignades();
            while (NumeroSolicitudsNoAssignades()!=0 && y<_vehicles.size()){
                //System.out.println(_vehicles.get(i));
                CrearRuta(_vehicles.get(i));
                _vehicles.get(i).restaurarCarrega();
                i++;
                if(anterior==NumeroSolicitudsNoAssignades()){
                y++;
                }else{
                    y=0;
                }
                anterior=NumeroSolicitudsNoAssignades();
                if(i==_vehicles.size()){
                    i=0;
                }
                System.out.println("===============================\n Numero de solicituds restants: " +  NumeroSolicitudsNoAssignades());
                
            }

        
        
        
        /*
        int indexVehicle = 0;
        int divisor = _vehicles.size();
        int numerador = _solicituds.size();
        int blocs = numerador / divisor;
        Iterator<Solicitud> iterador = _solicituds.iterator();
        for (int i = 0; i < divisor;i++) {
            Vehicle ve = _vehicles.get(indexVehicle);
            TreeSet<Solicitud> solici = new TreeSet<Solicitud>();
            int y = 0;
            while (iterador.hasNext() && y < blocs) {
                Solicitud s=iterador.next();
                solici.add(s);
                y++;
            }
            Pair<Vehicle, TreeSet<Solicitud>> subSol = new Pair<Vehicle, TreeSet<Solicitud>>(ve, solici);
            _ruta.add(subSol);
            indexVehicle++;
        }
        */
    }

     /**
     * @brief Mostra les solicituds per cada vehicle
     * @pre ---
     * @post Mostra les solicituds per cada vehicle
     */
    public void MostrarVehiclesSolicituds(){
        Iterator<Pair<Vehicle,TreeSet<Solicitud>>> it= _ruta.iterator();
        while(it.hasNext()){
            Pair<Vehicle, TreeSet<Solicitud>> pair = it.next();
            Vehicle v = pair.getKey();
            TreeSet<Solicitud> s = pair.getValue();
            System.out.println("************************************************\n Vehicle:");
            System.out.println(v.toString() + "\n Solicitds del vehicle:");
            System.out.println("\t" + s);
            System.out.println("************************************************\n");
        }
    }
    public void algoritmeBacktracking() {
        Collection<Edge> edgeSet = _graf.getEdgeSet();
        double pesTotalGraf = 0;
        for (Edge s : edgeSet) {
            double pes = s.getAttribute("Pes");
            System.out.println(pes);
        }
       
    }
    
     /**
     * @brief crea i mostra un subgraf complet amb les soliciutds del vehicle
     * @pre ---
     * @post Crea un subgraf amb node d'origen de Vehicle v i solicituds de llista_solicituds 
     */
    public Graph CrearSubGraf(Vehicle v,TreeSet<Solicitud> llista_solicituds) {
        Iterator<Solicitud> it = llista_solicituds.iterator();
        Graph subgraf = new SingleGraph("Ruta");
        //Primer afegim tots els depots
        for(int x=1;x<mapa.GetNumDepot();x++){            
                subgraf.addNode(_graf.getNode(x).getId());
                subgraf.getNode(_graf.getNode(x).getId()).setAttribute("ui.label", "Depot"+x);
        }
        while (it.hasNext()) {
            Solicitud s = it.next();
            int o = s.Origen();
            int d = s.Desti();
            String origen = Integer.toString(o);
            String desti = Integer.toString(d);
            if (subgraf.getNode(origen) == null) {
                subgraf.addNode(origen);
                subgraf.getNode(origen).setAttribute("ui.label", origen);
            }
            if (subgraf.getNode(desti) == null) {
                subgraf.addNode(desti);
                subgraf.getNode(desti).setAttribute("ui.label", desti);
            }

 
        }
        for(Node n: subgraf){
            for(Node m: subgraf){
                if(! n.hasEdgeBetween(m) && !n.equals(m)){
                    Double pes= _graf.getNode(n.getId()).getEdgeBetween(m.getId()).getAttribute("Pes");
                    subgraf.addEdge(Integer.toString(subgraf.getEdgeCount()), n, m);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount()-1)).addAttribute("pes", pes);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount()-1)).addAttribute("ui.label", pes);
                }
            }
        }
        subgraf=mapa.CompletarGraf(subgraf);
        //subgraf.display();
        return subgraf;
    }
    
    /**
     * @brief Donat un vehicle, crea una ruta de solicituds que el vehicle pot completar amb la seva autonomia total
     * @pre Vehicle valid
     * @post Afageix a _rutes, la ruta del vehicle v
     */
    public void CrearRuta(Vehicle v){
        TreeSet<Solicitud> ruta=new TreeSet<Solicitud>();
        Solicitud s=SolicitudMesProperaDisponible(v); //Busquem la solicitud mes propera al vehicle
        int contadorSolicituds=1;
        int posInicial=v.getPosicio();
        while(s!=null && contadorSolicituds<_solicituds.size()){
            if(VehiclePotAssolirSolicitud(v,s)){
                ruta.add(s);
                s.setEstat(Solicitud.ESTAT.ENTRANSIT);
            }
            contadorSolicituds++;
            s=SolicitudMesProperaDisponible(v);
        }
        v.setPosicio(posInicial);
      _rutes.add(new Ruta(v,ruta,CrearSubGraf(v, ruta)));
     
        
    }
    
    /**
     * @brief Retorna un array amb els nodes del graf
     * @pre ---
     * @post Retorna un array de nodes del graf g
     */
    public Object[] RetornarArrayNodes(Graph g){
        Collection<Node> array= g.getNodeSet();
        Object[] nodes=array.toArray();
        return nodes;
    }
    
    /**
     * @brief Retorna un array amb les arestes del graf
     * @pre ---
     * @post Retorna un array d'arestes del graf g
     */
    public Object[] RetornarArrayArestes(Graph g){
        Collection<Node> array= g.getNodeSet();
        Object[] arestes=array.toArray();
        return arestes;
    }
    
    /**
     * @brief Retorna la solicitud mes propera de vehicle v dins d'un rang preestablert
     * @pre ---
     * @post Retorna la solicitud mes propera de vehicle v dins d'un rang preestablert
     */
    public Solicitud SolicitudMesProperaDisponible(Vehicle v){
        Solicitud s=null;
        boolean trobat = false;
        Iterator<Solicitud> it = _solicituds.iterator();
        while (!trobat && it.hasNext()) {
            Solicitud ss = it.next();
            if (v.nodeInicial() == ss.Origen()) {
                trobat = true;
                s = ss;
            } else {                
                double pes = _graf.getNode(v.nodeInicial()).getEdgeBetween(ss.Origen()).getAttribute("Pes");
                if (pes < MAX_DISTANCIA_GREEDY && ss.getEstat() == Solicitud.ESTAT.ESPERA) {
                    trobat = true;
                    s = ss;
                }
            }
        }
        return s;
    }
    
    
    /**
     * @brief Diu si el vehicle pot anar a la solicitud, fer la solicitud, i tornar al Depot mes proper
     * @pre ---
     * @post Retorna cert si el vehicle v pot assolir la solicitud s
     */
    public boolean VehiclePotAssolirSolicitud(Vehicle v, Solicitud s) {
        boolean valid = false;
        double anar_solicitud;
        if (v.nodeInicial() == s.Origen()) {
            anar_solicitud = 0;
        } else {
            anar_solicitud = _graf.getNode(v.nodeInicial()).getEdgeBetween(s.Origen()).getAttribute("Pes");
        }
        double completar_solicitud = _graf.getNode(s.Origen()).getEdgeBetween(s.Desti()).getAttribute("Pes");
        double depot_proxim = BuscarDepotMesProxim(s.Desti());
        double autonomia = v.carregaRestant();
        if (anar_solicitud + completar_solicitud + depot_proxim < autonomia) {
            valid = true;
            v.descarga(anar_solicitud+completar_solicitud+depot_proxim);
            v.setPosicio(s.Desti());
        }
        return valid;
    }
    
    
    
    /**
     * @brief Retorna la distancia al depot mes proper del vehicle
     * @pre ---
     * @post Retorna la distancia al depot mes proper del vehicle v
     */
    public double BuscarDepotMesProxim(int index){
        double distancia=Integer.MAX_VALUE;
        int numDepots=0;
        Iterator<Node> it=_graf.iterator();
        boolean fi=false;
        while(it.hasNext() && !fi){
            Node n=it.next();
            if(n.getAttribute("Tipus").equals("Depot")){
                numDepots++;
            }
           
        }

        for (int i = 0; i < numDepots; i++) {
            if (index == i) {
                distancia = 0;
            } else {
                double pes = _graf.getNode(index).getEdgeBetween(i).getAttribute("Pes");
                if (pes < distancia) {
                    distancia = pes;
                }
            }
        }

        
        
        return  distancia;
    }
    
    
    /**
     * @brief Mostrar rutes
     * @pre ---
     * @post Mostra les rutes de _rutes
     */
    public void MostrarRutes(){
        for(Ruta r:_rutes){
            System.out.println(r);
            r.MostrarGraf();
        }
    }
    
    public void MostrarSolicitudsNoAssignades(){
        for(Solicitud s: _solicituds){
            if(s.getEstat()==Solicitud.ESTAT.ESPERA){
                System.out.println(s);
            }
        }
    }
    
     public int NumeroSolicitudsNoAssignades(){
        int noAssignades=0;
        for(Solicitud s: _solicituds){
            if(s.getEstat()==Solicitud.ESTAT.ESPERA){
                noAssignades++;
            }
        }
        return noAssignades;
    }
}
