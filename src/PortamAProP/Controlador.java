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
                    break;
                case 2:
                    algoritmeBacktracking();
                    break;
                case 3:
                    
                    break;
                case 4:
                    AssignarSolicitudsAVehicles();
                    MostrarVehiclesSolicituds();
                case 5:
                
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
        mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_D);//Els Depots sempre es Generer primer i a partir de un fitxer
        System.out.println("Com vols generar la resta del graf? Random o Fitxer [R/F]:");
        //Scanner teclat=new Scanner(System.in);
        //String opcio=teclat.nextLine();
        String opcio=FORMAT_ENTRADA_GRAF;
        
        if(opcio.equals("R")){
            _generadorNodes = new GeneradorNodesGraf();
            _generadorNodes.GeneradorAleatoriNodes(_graf.getNodeCount());
            String nodes=_generadorNodes.OptenirNodes();
            mapa.ModificarGrafPerString(_graf, nodes);    
        }else if(opcio.equals("F")){
            mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_G);
        }

        _graf = mapa.obtGraph();
        System.out.println("Nodes inserits correctament");

    }

     /**
     * @brief Assigna per cada vehicle, un grup de solicituds
     * @pre ---
     * @post Afageix a _ruta, vehicles amb unes solicituds 
     */
    public void AssignarSolicitudsAVehicles() {
        int indexVehicle = 0;
        int divisor = _vehicles.size();
        int numerador = _solicituds.size();
        int blocs = numerador / divisor;
        Iterator<Solicitud> iterador = _solicituds.iterator();
        for (int i = 0; i < blocs;i++) {
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

    }


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
            CrearSubGraf(v,s);
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

    public void CrearSubGraf(Vehicle v,TreeSet<Solicitud> llista_solicituds) {
        Iterator<Solicitud> it = llista_solicituds.iterator();
        Graph subgraf = new SingleGraph("Ruta");
        System.out.println("##########################################");
        subgraf.addNode(Integer.toString(v.nodeInicial()));
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
        subgraf.display();
    }

}
