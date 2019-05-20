package PortamAProP;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import javax.swing.JFrame;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/*
 * @brief Classe controladora, es dedica a gestionar les diferents opcions del
 * programa
 * @input Menu provisional per fer diferents execucions
 * @output ...
 * @author drive
 */
public class Controlador {

    private Graph _graf; // @brief Ens guardem el graf com atribut per executar els diferents algoritmes sobre ell
    private SortedSet<Peticio> _peticions; // @brief Vector de peticions, el fem servir en els diferents algoritmes
    private List<Vehicle> _vehicles;//@brief Estructura on ens guardem els vehicles
    private GeneradorNodesGraf _generadorNodes; // @brief Objecte que ens permet generar un conjunt de nodes aleatoriament
    private String NOM_FITXER_D = "Depots.txt";
    private List<Pair<Vehicle, TreeSet<Peticio>>> _ruta = new ArrayList<Pair<Vehicle, TreeSet<Peticio>>>(10);
    private LlegirFitxerGraf mapa;
    private Estadistics estadistic;

    private ArrayList<Ruta> _rutes;
    private Greedy voras;

    /**
     * VARIABLES DE CONTROL
     */
    private final int _maxDistanciaGreedy;
    private final long _maxFinestraTemps;
    private final int _maximEspera;
    private final int _minimLegal;
    private final int _nPeticions;
    private final int _maxPersones;
    private final String _nFitxerSol;
    private final int _nNodes;
    private final int _pesMaxim;
    private final String _nFitxerGraf;
    private final boolean _randomSol;
    private final boolean _randomNode;
    private final double _minCarga;
    private StringBuilder _log;

    /**
     * @brief Constructor amb parametres
     * @pre ---
     * @post S'ha construit un objecte controlador amb parametres:
     * @param tamanyFinestra Ens dona el tamany de la finestra de temps
     * @param maximEspera Ens diu quant de temps volen esperar maxim els clients
     * @param minimLegal Ens diu quin es el minim temps per poder atendre una
     * peticio dictat per llei
     * @param nPeticions Ens diu el nombre de peticions que volem generar
     * @param maxPersones Ens diu el nombre maxim de persones per peticio a
     * generar
     * @param nFitxerSol Ens diu el nom del fitxer on anar a buscar les
     * peticions
     * @param nNodes Ens diu el nombre maxim de nodes que volem generar
     * @param pesMaxim Ens diu el pes maxim de les arestes
     * @param nFitxerGraf Ens diu el nom del fitxer del graf
     * @param maxGreedy Ens diu la distancia maxima del greedy
     * @param randomSol Ens diu si la generacio de peticions sera aleatoria
     * @param randomNode Ens diu si la generacio de nodes sera aleatoria
     * @param minCarga Ens diu el minim de carrega que el vehicle ha de tenir en
     * % abans de considerar un punt de carrega com acceptable
     */
    public Controlador(int tamanyFinestra, int maximEspera, int minimLegal, int nPeticions, int maxPersones,
             String nFitxerSol, int nNodes, int pesMaxim, String nFitxerGraf, int maxGreedy, boolean randomSol, boolean randomNode, double minCarga) {

        _maxFinestraTemps = tamanyFinestra;
        _maxDistanciaGreedy = maxGreedy;
        _maximEspera = maximEspera;
        _minimLegal = minimLegal;
        _nPeticions = nPeticions;
        _maxPersones = maxPersones;
        _nFitxerSol = nFitxerSol;
        _nNodes = nNodes;
        _pesMaxim = pesMaxim;
        _nFitxerGraf = nFitxerGraf;
        _randomSol = randomSol;
        _randomNode = randomNode;
        _minCarga = minCarga;
        _graf = new SingleGraph("MAPA");
        _graf.setAutoCreate(true);
        _log = new StringBuilder();
        generarGraf();
        _peticions = new TreeSet<>();
        generarSolicituds();
        _vehicles = new ArrayList<>();
        generarVehicles();
        _ruta = new ArrayList<>(10);
        _rutes = new ArrayList<>();
        voras = new Greedy(_maxFinestraTemps, _maxDistanciaGreedy);
        
    }

    /**
     * @brief Inicialitza el nostre programa
     * @pre ---
     * @post S'ha inicialitzat el programa
     */
    public void init() {
        assignarSolicitudsAVehicles();
        estadistic();
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("output.txt");
            pw = new PrintWriter(fichero);

            pw.println(_log.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
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
        _log.append("\nVehicles creats correctament\n");
    }

    /**
     * @brief Inicialitza les peticions
     * @pre ---
     * @post S'han inicialitzats les peticions
     */
    private void generarSolicituds() {
        LlegirFitxerPeticions lFitxer = new LlegirFitxerPeticions(_graf);
        if (_randomSol) {
            GeneradorPeticions sol = new GeneradorPeticions(_nPeticions, _maxPersones, _nNodes);
            String lSol = sol.toString();
            //System.out.println(lSol);
            lFitxer.LlegirDeText(lSol);
        } else {
            lFitxer.LlegirDeFitxer(_nFitxerSol);
        }
        _peticions = lFitxer.obtSol();
    }

    /**
     * @brief Crea el graf
     * @pre ---
     * @post S'ha creat el graf
     */
    private void generarGraf() {

        mapa = new LlegirFitxerGraf();
        mapa.ModificarGrafPerFitxer(_graf, NOM_FITXER_D, "Depot");//Els Depots sempre es Generer primer i a partir de un fitxer

        if (_randomNode) {
            _generadorNodes = new GeneradorNodesGraf(_pesMaxim, _nNodes);
            _generadorNodes.generadorAleatoriNodes(_graf.getNodeCount());
            String nodes = _generadorNodes.OptenirNodes();
            mapa.ModificarGrafPerString(_graf, nodes, "peticions");
        } else {
            mapa.ModificarGrafPerFitxer(_graf, _nFitxerGraf, "peticions");
        }

        _graf = mapa.obtGraph();
        _log.append("\nNodes inserits correctament\n");

    }

    /**
     * @brief Assigna per cada vehicle, un grup de peticions, tambe assigna tots
     * els depots
     * @pre ---
     * @post Afageix a _ruta, vehicles amb unes peticions
     */
    public void assignarSolicitudsAVehicles() {

        //for (int i = 0; i < _vehicles.size(); i++) 
        int i = 0; //index del vehicle
        int y = 0; //numero de cops que es repeteix el numeor de peticions no assignades
        int indexruta = 0;
        int anterior = numeroSolicitudsNoAssignades();
        while (numeroSolicitudsNoAssignades() != 0 && y < _vehicles.size()) {
            while (numeroSolicitudsNoAssignades() != 0 && i < _vehicles.size()) { //Mentre hi hagin peticions sense assignar i, y sigui mes petit que el numero de vechiles ( ha mirat tots els vehicles)
                //System.out.println(_vehicles.get(i));
                // System.out.println("Iteracio/Id vehicle :\n" + _vehicles.get(i).toString());
                voras.crearRuta(_vehicles.get(i), _rutes, _peticions, _graf, mapa);
                _vehicles.get(i).restaurarCarrega(); //Restarura la carga ja que ha estat modificada al crear la ruta
                i++;
                //System.out.println("\n===============================\n Numero de peticions restants: " + numeroSolicitudsNoAssignades());
            }
            i = 0;
            for (Vehicle v : _vehicles) { //un cop la ruta ha estat creada, el vehicle torna al seu node inicial
                v.setPosicio(v.nodeInicial());
            }
            if (anterior == numeroSolicitudsNoAssignades()) {//Si les peticions que queden son les mateixes que el bucle anterior augmentem y, sino y=0
                y++;
            } else {
                y = 0;
            }
            anterior = numeroSolicitudsNoAssignades();

            for (int k = indexruta; k < _rutes.size(); k++) {
                algoritmeBacktracking(_rutes.get(indexruta));
                indexruta++;
            }
            //Assignacio de totes els peticions en rutes com a finalitzades
            for (Ruta r : _rutes) {
                for (Peticio s : r.getSol()) {
                    for (Peticio ss : _peticions) {
                        if (ss == s) {
                            ss.modificarEstat(Peticio.ESTAT.FINALITZADA);
                        }
                    }
                }
            }

        }
    }

    
    /**
     * @brief Inicialitza l'algoritme de backtracking
     * @pre S'han assignat peticions als vehicles dins de la finestra de temps
     * @post S'ha trobat la millor ruta dins d'una finestra temps
     */
    public void algoritmeBacktracking(Ruta r) {

        SolucioRuta solRuta = new SolucioRuta(r, _minimLegal, _maximEspera, _minCarga, _log);
        SolucionadorRuta soluRuta = new SolucionadorRuta(solRuta, _log);
        boolean trobat = soluRuta.existeixSolucio();
        if (trobat) {
            _log.append("Solucio trobada: \n");
            _log.append("Ruta actual: " + (_rutes.size() - 1) + "\n");
            r.mostrarRuta();
        } else {
            _log.append("No s'ha trobat solucio\n");
        }

    }

    /**
     * @brief crea i mostra un subgraf complet amb les soliciutds del vehicle
     * @pre ---
     * @post Crea un subgraf amb node d'origen de Vehicle v i peticions de
     * llista_solicituds
     */
    public Graph crearSubGraf(Vehicle v, TreeSet<Peticio> llista_solicituds, int[] c) {
        Iterator<Peticio> it = llista_solicituds.iterator();
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
            Peticio s = it.next();
            int o = s.origen();
            int d = s.desti();
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
        for (Peticio s : _peticions) {
            if (s.obtenirEstat() == Peticio.ESTAT.ESPERA) {
                System.out.println(s);
            }
        }
    }

    public int numeroSolicitudsNoAssignades() {
        int noAssignades = 0;
        for (Peticio s : _peticions) {
            if (s.obtenirEstat() == Peticio.ESTAT.ESPERA || s.obtenirEstat() == Peticio.ESTAT.VISITADA) {
                noAssignades++;
            }
        }
        return noAssignades;
    }

    public void estadistic() {
        estadistic = new Estadistics(_rutes);
        double mitjanaRutaVehicle = estadistic.mitjanaTempsMarxaVehicle();
        double mitjanaCarragaVehicle = estadistic.mitjanaTempsCarregaVehicle();
        double mitjanaDistancia = estadistic.mitjanaDistanciaNodes();
        double mitjanaPassatgers = estadistic.mitjanaPassatgers();
        double mitjanaEsperaClient = estadistic.mitjanaTempsEsperaClient();
        double mitjanaMarxaClient = estadistic.mitjanaTempsMarxaClient();
        _log.append("\n====================ESTADISTICS GENERLAS====================\n");
        _log.append("MITJANA DE TEMPS QUE ELS VEHICLES ESTAN A LA CARRATERA: " + String.format("%.2f", mitjanaRutaVehicle) + " minuts\n"
                + "MITJANA DE TEMPS QUE ELS VEHILCES ESTAN CARREGAN: " + String.format("%.2f", mitjanaCarragaVehicle) + " minuts\n"
                + "MITJANA DE TEMPS QUE HI HA ENTRE ELS NODES: " + String.format("%.2f", mitjanaDistancia) + "minuts\n"
                + "MITJANA DE PASSATGERS: " + String.format("%.2f", mitjanaPassatgers) + " passatgers\n"
                + "MITJANA DE TEMPS QUE ELS CLIENTS HAN DE ESPERAR: " + String.format("%.2f", mitjanaEsperaClient) + " minuts\n"
                + "MITJANA DE TEMPS QUE ELS CLIENTS TARDEN A FER EL RECORREGUT: " + String.format("%.2f", mitjanaMarxaClient) + " minuts\n");

        System.out.println("====================ESTADISTICS GENERLAS====================");
        System.out.println("MITJANA DE TEMPS QUE ELS VEHICLES ESTAN A LA CARRATERA: " + String.format("%.2f", mitjanaRutaVehicle) +  " minuts\n"
                + "MITJANA DE TEMPS QUE ELS VEHILCES ESTAN CARREGAN: " + String.format("%.2f", mitjanaCarragaVehicle) + " minuts\n"
                + "MITJANA DE TEMPS QUE HI HA ENTRE ELS NODES: " + String.format("%.2f", mitjanaDistancia) + " minuts\n"
                + "MITJANA DE PASSATGERS: " + String.format("%.2f", mitjanaPassatgers) + " passatgers\n"
                + "MITJANA DE TEMPS QUE ELS CLIENTS HAN DE ESPERAR: " + String.format("%.2f", mitjanaEsperaClient) + " minuts\n"
                + "MITJANA DE TEMPS QUE ELS CLIENTS TARDEN A FER EL RECORREGUT: " + String.format("%.2f", mitjanaMarxaClient) + " minuts\n");
        for (Ruta r : _rutes) {
            if (r.finalitzada()) {
                //r.mostrarRutaSugraf();
            }
        }
        int finalitzades = 0;
        int noFinalitzades = 0;
        for (Ruta r : _rutes) {
            if (r.finalitzada()) {
                finalitzades++;
            } else {
                noFinalitzades++;
            }
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Finalitzades", finalitzades);
        dataset.setValue("No Finalitzades", noFinalitzades);
        JFreeChart chart = ChartFactory.createPieChart("Rutes", dataset);
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _log.append("Nombre de rutes finalitzades: " + finalitzades + "\n");
        _log.append("Nombre de rutes no finalitzades: " + noFinalitzades + "\n");
    }

}
