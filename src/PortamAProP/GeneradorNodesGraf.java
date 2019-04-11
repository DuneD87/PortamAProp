package PortamAProP;
import com.sun.xml.internal.ws.client.ContentNegotiation;
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

    private SortedSet<String> _ubicacions = new TreeSet<String>();
    private int _pesMax = 100;//Pes maxim que pot tenir una aresta
    private String nodes = "";  
   
    public void init( int nodesExistents) {

        Llegir_ubicacions();

        System.out.println("Quants nodes vols crear?");
        Scanner teclat = new Scanner(System.in);
        int numNodes = Integer.parseInt(teclat.nextLine());
        nodes = "";
        for (int i = nodesExistents; i <= numNodes+nodesExistents; i++) {//Per cada node assignar id i etiqueta
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
        
        ///////////////////////////////////////////////////////////////////////////
        ////////////////////RecordarCambiarR
        TornarString();
    }

    //Pre: cert
    //Post: Emplena un Set amb ubicacions a partir de un fitxer
    public void Llegir_ubicacions() {
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            System.out.println("Nom amb el fitxer de localitzacions:");
            Scanner teclat = new Scanner(System.in); 
            String nom=teclat.nextLine();
            fitxer = new File(nom);
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

    //Pre: -
    //Post: Crea un fitxer de nom nomf i amb el contingut de sol
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
    
    public String TornarString(){
        return nodes;
    }

}
