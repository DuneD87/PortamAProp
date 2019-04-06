package proprotest;

import java.io.File;
import java.util.Scanner;


/**
 * @brief Classe controladora, es dedica a gestionar les diferents opcions del programa
 * @input Menu provisional per fer diferents execucions
 * @output ...
 * @author drive
 */
public class Controlador {
    /**
    * @brief Constructor per defecte
    * @pre ---
    * @post S'ha construit un objecte controlador per defecte
    */
    public Controlador() {
        
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
                    System.out.println("test1");
                    break;
                case 2:
                    System.out.println("test2");
                    //TODO: Generar fitxer solicitud
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
}
