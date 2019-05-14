package PortamAProP;

import java.io.File;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.text.StyledEditorKit;
import jdk.nashorn.internal.objects.NativeJava;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import scala.Int;

/*
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
    private String NOM_FITXER_D = "Depots.txt";
    private String NOM_FITXER_G = "Graf.txt";
    private String FORMAT_ENTRADA_GRAF = "R";
    private String FORMAT_ENTRADA_SOLICITUDS = "R";
    private List<Pair<Vehicle, TreeSet<Solicitud>>> _ruta = new ArrayList<Pair<Vehicle, TreeSet<Solicitud>>>(10);
    private LlegirFitxerGraf mapa;
    private Object[] _nodes;
    private Object[] _arestes;
    private int MAX_DISTANCIA_GREEDY = 100;//@brief distancia maxima acceptada pel greedy
    private ArrayList<Ruta> _rutes;
    private long LIMIT_FINESTRA_TEMPS = 500;//@brief Temps de la finestra de temps en algoritme greedy (Temps en MINUTS)
    private Greedy voras;
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
        _ruta = new ArrayList<Pair<Vehicle, TreeSet<Solicitud>>>(10);
        _rutes = new ArrayList<Ruta>();
        voras=new Greedy(LIMIT_FINESTRA_TEMPS, MAX_DISTANCIA_GREEDY);
    }

    /**
     * @brief Inicialitza el nostre programa
     * @pre ---
     * @post S'ha inicialitzat el programa
     */
    public void init() {
        //assignarSolicitudsAVehicles();
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
                    for (int i = 0; i < _graf.getNodeCount(); i++) {
                        System.out.println("Id: " + _graf.getNode(i).getId() + " Vehicles actuals: " + _graf.getNode(i).getAttribute("VehiclesActual"));
                    }
                    break;
                case 2:
                    algoritmeBacktracking(_rutes.get(0));
                    break;
                case 3:

                    break;
                case 4:
                    //_graf.display();
                    assignarSolicitudsAVehicles();
                    //mostrarVehiclesSolicituds();
                    //mostrarSolicitudsNoAssignades();
                    break;
                case 5:
                    mostrarSolicitudsNoAssignades();
                    break;
                case 6:
                    mostrarRutes();
                    break;
                case 0:
                    System.exit(1);
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
                + "4 - Obrir finestra de temps\n"
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

        for (int i = 0; i < _vehicles.size(); i++) {//suma 1 per cada vehicle en el su depot
            Vehicle v = _vehicles.get(i);
            //System.out.println(v.nodeInicial());
            Integer n = Integer.parseInt(_graf.getNode(v.nodeInicial()).getAttribute("VehiclesActual")) + 1;
            String s = n.toString();
            _graf.getNode(v.nodeInicial()).setAttribute("VehiclesActual", s);
        }
    }

    /**
     * @brief Inicialitza les solicituds
     * @pre ---
     * @post S'han inicialitzats les solicituds
     */
    private void generarSolicituds() {
        LlegirFitxerSolicitud lFitxer = new LlegirFitxerSolicitud(_graf);
        if (FORMAT_ENTRADA_SOLICITUDS.equals("R")) {
            GeneradorSolicituds sol = new GeneradorSolicituds();
            String lSol = sol.toString();
            //System.out.println(lSol);
            lFitxer = new LlegirFitxerSolicitud(lSol, _graf);
        } else if (FORMAT_ENTRADA_SOLICITUDS.equals("F")) {
            // Ja esta fet a la inicialitzacio del objecte lFitxer
        }
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

        if (FORMAT_ENTRADA_GRAF.equals("R")) {
            _generadorNodes = new GeneradorNodesGraf();
            _generadorNodes.GeneradorAleatoriNodes(_graf.getNodeCount());
            String nodes = _generadorNodes.OptenirNodes();
            mapa.ModificarGrafPerString(_graf, nodes, "Solicitud");
        } else if (FORMAT_ENTRADA_GRAF.equals("F")) {
            mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_G, "Solicitud");
        }

        _graf = mapa.obtGraph();
        System.out.println("Nodes inserits correctament");

    }

    /**
     * @brief Assigna per cada vehicle, un grup de solicituds, tambe assigna
     * tots els depots
     * @pre ---
     * @post Afageix a _ruta, vehicles amb unes solicituds
     */
    public void assignarSolicitudsAVehicles() {

        //for (int i = 0; i < _vehicles.size(); i++) 
        int i = 0; //index del vehicle
        int y = 0; //numero de cops que es repeteix el numeor de solicituds no assignades
        int indexruta = 0;
        int anterior = numeroSolicitudsNoAssignades();
        while (numeroSolicitudsNoAssignades() != 0 && y<_vehicles.size()) {
            while (numeroSolicitudsNoAssignades() != 0 && i < _vehicles.size()) { //Mentre hi hagin solicituds sense assignar i, y sigui mes petit que el numero de vechiles ( ha mirat tots els vehicles)
                //System.out.println(_vehicles.get(i));
                // System.out.println("Iteracio/Id vehicle :\n" + _vehicles.get(i).toString());
                voras.crearRuta(_vehicles.get(i), _rutes, _solicituds, _graf, mapa);
                _vehicles.get(i).restaurarCarrega(); //Restarura la carga ja que ha estat modificada al crear la ruta
                i++;
                //System.out.println("\n===============================\n Numero de solicituds restants: " + numeroSolicitudsNoAssignades());
            }
            i=0;
            for (Vehicle v : _vehicles) { //un cop la ruta ha estat creada, el vehicle torna al seu node inicial
                v.setPosicio(v.nodeInicial());
            }
            if (anterior == numeroSolicitudsNoAssignades()) {//Si les solicituds que queden son les mateixes que el bucle anterior augmentem y, sino y=0
                    y++;
                } else {
                    y = 0;
                }
             anterior = numeroSolicitudsNoAssignades();
            
            for (int k = indexruta; k < _rutes.size(); k++) {
                algoritmeBacktracking(_rutes.get(indexruta));
                indexruta++;
            }
            //Assignacio de totes els solicituds en rutes com a finalitzades
            for (Ruta r: _rutes){
                for(Solicitud s: r.getSol()){
                    for(Solicitud ss: _solicituds){
                        if(ss==s){
                            ss.setEstat(Solicitud.ESTAT.FINALITZADA);
                        }
                    }
                }
            }
             
        }
        

        /*
        //GREEDY SENSILL
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
    public void mostrarVehiclesSolicituds() {
        Iterator<Pair<Vehicle, TreeSet<Solicitud>>> it = _ruta.iterator();
        while (it.hasNext()) {
            Pair<Vehicle, TreeSet<Solicitud>> pair = it.next();
            Vehicle v = pair.getKey();
            TreeSet<Solicitud> s = pair.getValue();
            System.out.println("************************************************\n Vehicle:");
            System.out.println(v.toString() + "\n Solicitds del vehicle:");
            System.out.println("\t" + s);
            System.out.println("************************************************\n");
        }
    }

    /**
     * @brief Inicialitza l'algoritme de backtracking
     * @pre S'han assignat solicituds als vehicles dins de la finestra de temps
     * @post S'ha trobat la millor ruta dins d'una finestra temps
     */
    public void algoritmeBacktracking(Ruta r) {


        LocalTime horaActual = LocalTime.MIDNIGHT;
        SolucioRuta solRuta = new SolucioRuta(r,horaActual);
        SolucionadorRuta soluRuta = new SolucionadorRuta(solRuta);
        boolean trobat = soluRuta.existeixSolucio();
        if (trobat) {
            System.out.println("Solucio trobada: ");
            r.mostrarRuta();
        } else {
            System.out.println("No s'ha trobat solucio");
            return;
        }
     
    }

    /**
     * @brief crea i mostra un subgraf complet amb les soliciutds del vehicle
     * @pre ---
     * @post Crea un subgraf amb node d'origen de Vehicle v i solicituds de
     * llista_solicituds
     */
    public Graph crearSubGraf(Vehicle v, TreeSet<Solicitud> llista_solicituds, int[] c) {
        Iterator<Solicitud> it = llista_solicituds.iterator();
        Graph subgraf = new SingleGraph("Ruta");
        //Primer afegim tots els depots
        for (int x = 0; x < mapa.GetNumDepot(); x++) {
            subgraf.addNode(_graf.getNode(x).getId());
            subgraf.getNode(_graf.getNode(x).getId()).setAttribute("ui.label", "Depot" + x);
            subgraf.getNode(_graf.getNode(x).getId()).addAttribute("Tipus", "Depot");
            subgraf.getNode(_graf.getNode(x).getId()).addAttribute("Nom", _graf.getNode(x).getAttribute("Nom").toString());
            c[_graf.getNode(x).getIndex()] = x;

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
                subgraf.getNode(_graf.getNode(origen).getId()).addAttribute("Nom", _graf.getNode(origen).getAttribute("Nom").toString());
                c[_graf.getNode(origen).getIndex()] = subgraf.getNodeCount() - 1;

            }
            if (subgraf.getNode(desti) == null) {
                subgraf.addNode(desti);
                subgraf.getNode(_graf.getNode(desti).getId()).addAttribute("Nom", _graf.getNode(desti).getAttribute("Nom").toString());
                subgraf.getNode(desti).setAttribute("ui.label", desti);
                c[_graf.getNode(desti).getIndex()] = subgraf.getNodeCount() - 1;
            }

        }
        for (Node n : subgraf) {
            for (Node m : subgraf) {
                if (!n.hasEdgeBetween(m) && !n.equals(m)) {
                    Double pes = _graf.getNode(n.getId()).getEdgeBetween(m.getId()).getAttribute("Pes");
                    subgraf.addEdge(Integer.toString(subgraf.getEdgeCount()), n, m);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount() - 1)).addAttribute("pes", pes);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount() - 1)).addAttribute("ui.label", pes);
                }
            }
        }
        subgraf = mapa.CompletarGraf(subgraf);
        //subgraf.display();
        return subgraf;
    }



    /**
     * @brief Retorna un array amb els nodes del graf
     * @pre ---
     * @post Retorna un array de nodes del graf g
     */
    /*
    public Object[] retornarArrayNodes(Graph g){
        Collection<Node> array= g.getNodeSet();
        Object[] nodes=array.toArray();
        return nodes;
    }
     */
    /**
     * @brief Retorna un array amb les arestes del graf
     * @pre ---
     * @post Retorna un array d'arestes del graf g
     */
    /*
    public Object[] retornarArrayArestes(Graph g){
        Collection<Node> array= g.getNodeSet();
        Object[] arestes=array.toArray();
        return arestes;
    }
     */
    /**
     * @brief Retorna la solicitud mes propera de vehicle v dins d'un rang
     * preestablert
     * @pre ---
     * @post Retorna la solicitud mes propera de vehicle v dins d'un rang
     * preestablert
     */


    /**
     * @brief Diu si el vehicle pot anar a la solicitud, fer la solicitud, i
     * tornar al Depot mes proper
     * @pre ---
     * @post Retorna cert si el vehicle v pot assolir la solicitud s
     */


    /**
     * @brief Mostrar rutes
     * @pre ---
     * @post Mostra les rutes de _rutes
     */
    public void mostrarRutes() {
        for (Ruta r : _rutes) {
            System.out.println(r);
            r.MostrarGraf();
        }
    }

    public void mostrarSolicitudsNoAssignades() {
        for (Solicitud s : _solicituds) {
            if (s.getEstat() == Solicitud.ESTAT.ESPERA) {
                System.out.println(s);
            }
        }
    }

    public int numeroSolicitudsNoAssignades() {
        int noAssignades = 0;
        for (Solicitud s : _solicituds) {
            if (s.getEstat() == Solicitud.ESTAT.ESPERA || s.getEstat() == Solicitud.ESTAT.VISITADA) {
                noAssignades++;
            }
        }
        return noAssignades;
    }

}
