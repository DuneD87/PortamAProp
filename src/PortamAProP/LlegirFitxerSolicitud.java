package PortamAProP;

/**
 * @brief Objecte encarregat de llegir les solicituds i afegirles a la estructura de dades
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import org.graphstream.graph.*;
public class LlegirFitxerSolicitud {

    private SortedSet<Solicitud> _vecSol; //@brief Estructura on ens guardem les solicituds ordenades per data d'emissio decreixent
    private Graph _graf;
    private String NOM_FITXER_SOL="Solicituds.txt"; //@brief Nom del fitxer d'on es llegeixen les solicituds
    /**
     * @brief Inicialitza la lectura de solicituds
     * @pre Cadena de caracters amb el format implicit
     * @post S'han llegit les solicituds desde cadena de caracters
     */
    public LlegirFitxerSolicitud(String text, Graph graf) {
        _graf=graf;
        _vecSol = new TreeSet<Solicitud>();
        String[] lines = text.split("\r\n|\r|\n");
        for (int i = 0; i < lines.length; i++) {
            CrearSolicitud(lines[i]);
        }
        
        System.out.println("Solicituds creades");
        
    }
    
    /**
     * @brief Inicialitza la lectura de solicituds
     * @pre ---
     * @post S'han llegit les solicituds desde fitxer
     */
    public LlegirFitxerSolicitud(Graph graf) {
        _vecSol = new TreeSet<Solicitud>();
        _graf=graf;
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            Scanner sc = new Scanner(System.in);
            //System.out.println("Nom del fitxer de solicituds:");
            //String nom = sc.nextLine();
            String nom=NOM_FITXER_SOL;
            fitxer = new File(nom);
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);

            String linia;
            while ((linia = br.readLine()) != null) {
                if(linia.charAt(0)!='#')
                    CrearSolicitud(linia);
         
            }
            for (Solicitud s : _vecSol) {
                //System.out.println(s);
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
     * @brief Crea una solicitud
     * @pre s valid per a configuracio de solicitud
     * @post S'ha creat una nova solicitud i s'ha afegit a l'estructura
     */
    private void CrearSolicitud(String s) {
        String[] parts = s.split(" ");
        if(_graf.getNode(parts[1])!=null &&_graf.getNode(parts[2])!=null && _graf.getNode(parts[1]).hasEdgeBetween(parts[2])){
        LocalTime emisio = LocalTime.of(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Solicitud sol = new Solicitud(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), emisio, Integer.parseInt(parts[6]));
        _vecSol.add(sol);
        }else
            System.out.println("Solicitud no creada perque els nodes no existeixen");

    }
    
    /**
     * @brief Estructura de solicituds
     * @pre S'ha cridat initText o initFitxer
     * @post Ens dona l'estructura ordenada que conte les solicituds
     */
    public SortedSet<Solicitud> obtSol() {
        return _vecSol;
    }
}