package PortamAProP;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @class GeneradorSolicituds
 * @brief Generador intern de peticions aleatories.
 * -Donat 3 numeros, determinem:
 * --maxSol: Ens diu el nombre maxim de peticions que es crearan de forma aleatoria
 * --maxPersones: Ens diu el nombre maxim de persones per peticio
 * --nNodes: Ens dona el nombre maxim de nodes amb el que treballarem
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class GeneradorPeticions {

  
    private final int _maxNodes;//@brief nombre maxim de nodes
    private String _sol;//@brief Cadena de caracters que guarda el contingut de generar peticions aleatories
    private final int _maxSol;//@brief Maxim nombre de peticions a crear
    private final int _maxPersones;//@brief Maxim nombre de persones per peticio
    
    /**
     * @brief Constructor per defecte
     * @param maxSol Ens diu el nombre maxim de peticions que es crearan de forma aleatoria
     * @param maxPersones Ens diu el nombre maxim de persones per peticio
     * @param nNodes Ens dona el nombre maxim de nodes amb el que treballarem
     */
    public GeneradorPeticions(int maxSol, int maxPersones, int nNodes) {
        _maxSol = maxSol;
        _maxPersones = maxPersones;
        _maxNodes = nNodes;
        _sol = crearSolicituds();
    }

    /**
     * @brief Metode que genera un String complet amb peticionsAleatories
     * 
     *      Per generar peticions de forma aleatoria, el metode crea un bucle entre 0 i el maxim de solicituds (_maxSol):
     *      Per cada iteracio genera dos index diferents de nodes, els posa en un string, siguidament concatena en el string el 
     *      temps de emisio generat aleatoriament i el nombre de passatgers de la peticio tambe
     *      generat aleatoriament, finalment isereix un salt de linia
     * 
     * @post Retorna un String amb el format correcte de peticions
     */
    
    private String crearSolicituds() {
        Random random = new Random();
        String llistat = "";
        for (int i = 0; i < _maxSol; i++) {
            llistat += i + " ";
            int random1 = random.nextInt(_maxNodes);
            int random2;
            do {
                random2 = random.nextInt(_maxNodes);
            } while (random1 == random2);
            llistat += random1 + " ";//id node sortida
            llistat += random2 + " ";//id node entrada
            llistat += random.nextInt(24) + " ";//hora emisio
            llistat += random.nextInt(60) + " ";//minut emisio
            llistat += random.nextInt(60) + " ";//segon emisio
            llistat += random.nextInt(_maxPersones) + 1;//numero de passatgers, entre 1 i 9
            llistat += "\n";

        }
        return llistat;
    }
     /**
      * @brief El metode Crea un fitxer amb peticions amb el format correcte
      * @param nomf Nom del fitxer de sortida
      * @param sol  String amb les peticions
      */
    public void crearFitxer(String nomf, String sol) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(nomf);
            pw = new PrintWriter(fichero);

            pw.println(sol);

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
     * @brief Ens dona les peticions generades aleatoriament
     * @pre ---
     * @return Cadena de caracters amb les peticions
     */
    @Override
    public String toString() {
        return _sol;
    }
}