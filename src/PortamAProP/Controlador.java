package PortamAProP;

import java.io.File;
import java.sql.Time;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.Vector;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;


/**
 * @brief Classe controladora, es dedica a gestionar les diferents opcions del programa
 * @input Menu provisional per fer diferents execucions
 * @output ...
 * @author drive
 */
public class Controlador {
    
    private Graph _graf; // @brief Ens guardem el graf com atribut per executar els diferents algoritmes sobre ell
    private SortedSet<Solicitud> _solicituds; // @brief Vector de solicituds, el fem servir en els diferents algoritmes 
    private GeneradorNodesGraf _generadorNodes; // @brief Objecte que ens permet generar un conjunt de nodes aleatoriament
    private GeneradorSolicituds _generadorSol; // @brief Objecte que ens permet generar un conjunt de solicituds aleatoriament

    /**
    * @brief Constructor per defecte
    * @pre ---
    * @post S'ha construit un objecte controlador per defecte
    */
    public Controlador() {
        _graf = new SingleGraph("MAPA");
        _generadorNodes = new GeneradorNodesGraf();
        _generadorSol = new GeneradorSolicituds();
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
                    crearFitxerNode();
                    break;
                case 2:
                    crearFitxerSolicitud();
                    break;
                case 3:
                    System.out.println("test3");
                    GenerarGraf();
                    break;
                case 4:
                    System.out.println("test4");
                    //TODO: Llegir fitxer vehicles
                    break;
                case 5:
                    System.out.println("test5");
                    //TODO: Llegir fitxer solicituds
                    break;
                case 6:
                    _graf.display();
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
                + "1 - Generar fitxer nodes\n"
                + "2 - Generar fitxer solicitud\n"
                + "3 - Llegir fitxer graf\n"
                + "4 - Llegir fitxer vehicles\n"
                + "5 - Llegir fitxer solucituds\n"
                + "6 - Mostrar Graf \n"
                + "0 - Sortir");
    }
    
    /**
     * @brief Inicialitza el graf
     * @pre ---
     * @post S'ha incialitzat el graf
     */
    public void initGraf(File fitxer) {
        
    }
    
    /**
     * @brief Inicialitza els vehicles
     * @pre ---
     * @post S'han incialitzat els vehicles i guardats en una estructura de dades
     */
    public void initVehicles(File fitxer) {
        
    }
    
    /**
     * @brief Inicialitza les solicituds
     * @pre ---
     * @post S'han inicialitzats les solicituds 
     */
    public void initSolicitud(File fitxer) {
        
    }
    
    /**
     * @brief Crea un fitxer amb nodes
     * @pre ---
     * @post S'ha generat un fitxer TFG
     */
    public void crearFitxerNode() {
       
 
    }
    
    /**
     * @brief Crea un fitxer amb solicituds
     * @pre ---
     * @post S'ha creat un fitxer amb solicituds
     */
    public void crearFitxerSolicitud() {
        
  
    }
    
     
    /**
     * @brief Crea el graf
     * @pre ---
     * @post S'ha creat el graf
     */
    public void GenerarGraf(){
       LlegirFitxerGraf mapa= new LlegirFitxerGraf(_graf);
       _graf = mapa.obtGraph();
       System.out.println("Nodes inserits correctament");
        
    }
}
