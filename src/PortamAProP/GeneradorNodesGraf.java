package PortamAProP;
/**
 * @class GeneradorNodesGraf
 * @brief Classe que ens permet generar nodes del graf
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.Scanner;
import java.util.TreeSet;

public class GeneradorNodesGraf {

    private SortedSet<String> _ubicacions;
    private int _pesMax = 100;//Pes maxim que pot tenir una aresta
    
   /**
    * @brief Inicialitza la generacio de nodes 
    * @pre ---
    * @post S'ha inicialitzat la generacio de nodes
    */
    public GeneradorNodesGraf() {
        _ubicacions = new TreeSet<>();
        Llegir_ubicacions();

        System.out.println("Quants nodes vols crear?");
        Scanner teclat = new Scanner(System.in);
        int numNodes = Integer.parseInt(teclat.nextLine());
        String nodes = "";
        for (int i = 1; i <= numNodes; i++) {//Per cada node assignar id i etiqueta
            nodes += Integer.toString(i) + " ";
            nodes += _ubicacions.first() + "\n";
            _ubicacions.remove(_ubicacions.first());
        }
        nodes += "#\n";
        int maximArestes = numNodes * (numNodes - 1) / 2;
        Random random = new Random();
        int numArestes = random.nextInt(maximArestes) + 1;
        for (int i = 1; i <= numArestes; i++) {
            int origen = random.nextInt(numNodes) + 1;
            int desti = 0;
            do {
                desti = random.nextInt(numNodes) + 1;
            } while (origen == desti);
            nodes += Integer.toString(origen) + " ";
            nodes += Integer.toString(desti) + " ";
            nodes += Integer.toString(random.nextInt(_pesMax) + 1) + "\n";
        }
        nodes += "#\n";
        System.out.println(nodes);
        System.out.println("Vols fer un fitxer amb aqustes solicituds? [S|n]");
        String eleccio = teclat.nextLine();
        if (eleccio.equals("S")) {
            System.out.println("Nom del fitxer:");
            String nomf = teclat.nextLine();
            CrearFitxer(nomf, nodes);
            
        }
    }

    /**
     * @brief Llegeix les ubicacions
     * @pre ---
     * @post S'han llegit les ubicacions i s'han guardat en una estructura de dades
     */
    private void Llegir_ubicacions() {
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
           
            fitxer = new File("PilaUbicacions.txt").getAbsoluteFile();
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);

            String linia;
            while ((linia = br.readLine()) != null) {
                _ubicacions.add(linia);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
    }

    /**
     * @brief Crea un fitxer amb els nodes
     * @pre ---
     * @S'ha creat un fitxer al directori local amb format TFG 
     */
    public void CrearFitxer(String nomf, String sol) {
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

}
