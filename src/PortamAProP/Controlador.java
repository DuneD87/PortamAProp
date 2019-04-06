package PortamAProP;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import org.graphstream.graph.Graph;


/**
 * @brief Classe controladora, es dedica a gestionar les diferents opcions del programa
 * @input Menu provisional per fer diferents execucions
 * @output ...
 * @author drive
 */
public class Controlador {
    
    private Graph _graf; // @brief Ens guardem el graf com atribut per executar els diferents algoritmes sobre ell
    private Vector<Solicitud> _solicituds; // @brief Vector de solicituds, el fem servir en els diferents algoritmes 
    
    /**
    * @brief Constructor per defecte
    * @pre ---
    * @post S'ha construit un objecte controlador per defecte
    */
    public Controlador() {
        //Redundant
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
                    //TODO: Llegir fitxer graf
                    break;
                case 4:
                    System.out.println("test4");
                    //TODO: Llegir fitxer vehicles
                    break;
                case 5:
                    System.out.println("test5");
                    //TODO: Llegir fitxer solicituds
                    break;
            }
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
                + "5 - Llegir fitxer solucituds\n");
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
        GeneradorNodesGraf g = new GeneradorNodesGraf();
        g.init();
    }
    
    /**
     * @brief Crea un fitxer amb solicituds
     * @pre ---
     * @post S'ha creat un fitxer amb solicituds
     */
    public void crearFitxerSolicitud() {
        GeneradorSolicituds g = new GeneradorSolicituds();
        g.init();
    }
}
