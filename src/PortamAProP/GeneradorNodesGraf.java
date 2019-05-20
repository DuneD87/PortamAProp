package PortamAProP;

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
/**
 * @class GeneradorNodesGraf
 * @brief Generador intern de grafs aleatoris
 * -Donat 2 numeros, determinem:
 * --pesMax: Ens diu el pes maxim que poden tenir les arestes del graf
 * --maxNodes: Ens diu el numero maxim de nodes del graf
 * @author Xavier Avivar & Buenaventura Martinez
 */



public class GeneradorNodesGraf {

    private SortedSet<String> _ubicacions;//@brief pila per emmagatzemar els noms dels nodes
    private final int _pesMax;//@brief Pes maxim que pot tenir una aresta
    private final int _maxNodes;//@brief Numero maxim de nodes que pot tenir el graf
    private final String _fitxerUbicacions="PilaUbicacions.txt";//@brief Nom del fitxer on anira a buscar els nomos dels nodes del graf
    private  String _nodes="";//@brief String on es guardara els nodes del graf amb el format corresponent
    
   /**
    * @brief Inicialitza la generacio de nodes 
    * @pre ---
    * @post S'ha inicialitzat la generacio de nodes
    */
    public GeneradorNodesGraf(int pesMax, int maxNodes) {
        _pesMax = pesMax;
        _maxNodes = maxNodes;
    }
    
    /**
     * @brief Generador de nodes aleatori
     * 
     *      Per Generar nodes aleatoris, el que fa primer de tot es guardar el nom 
     *      dels nodes a una estructura de dades, en aquest cas un TreeSet, seguidament es fa un bucle for, desde NodesExistents (numero de depots que hi ha al graf) 
     *      fins al maxim de nodes+NodesExistents (es fa la suma perque el numero de nodes que entrem per paramentre no inclu els depots).
     *      Per cada iteracio, sumem al string _nodes el index del node, el nom ( agafem el primer de la estructura que conte els noms, i el borrem).
     *      Finalment possa un #.
     *      Tot seguit generem les Arestes.
     *      Per saber el nombre de arestes maximes que pot tenir un graf utilitzem la formula MaxArestes=NumNodes*(NumNodes-1)/2, 
     *      Despres fem un bucle entre 1 i el nombre maxim de aretes:
     *      Per cada iteracio generem l'index del primer node de forma aleatoria,
     *      per generar el segon index fem un bucle que vagi generant index fins que 
     *      sigui diferent al primer.
     *      Tot seguit afageig en el String _nodes els dos index de node + un pes 
     *      generat de forma aleatoria entre 1 i _pesMaxim
     *      
     * 
     * @pre  NodesExistents>0
     * @post S'ha generat un String amb nodes aleatoris
     */
    public void  generadorAleatoriNodes(int NodesExistents){
        _ubicacions = new TreeSet<>();
        llegirUbicacions();

        //System.out.println("Quants nodes vols crear?");
        //Scanner teclat = new Scanner(System.in);
        //int numNodes = Integer.parseInt(teclat.nextLine());
        int numNodes = _maxNodes;
        _nodes = "";
        for (int i = NodesExistents; i <= numNodes+NodesExistents; i++) {//Per cada node assignar id i etiqueta
            _nodes += Integer.toString(i) + " ";
            _nodes += _ubicacions.first() + "\n";
            _ubicacions.remove(_ubicacions.first());
        }
        _nodes += "#\n";
        int maximArestes = numNodes * (numNodes - 1) / 2;
        Random random = new Random();
        int numArestes = random.nextInt(maximArestes) + 1;
        for (int i = 1; i <= numArestes; i++) {
            int origen = random.nextInt(numNodes) + 1;
            int desti = 0;
            do {
                desti = random.nextInt(numNodes) + 1;
            } while (origen == desti);
            _nodes += Integer.toString(origen) + " ";
            _nodes += Integer.toString(desti) + " ";
            _nodes += Integer.toString(random.nextInt(_pesMax) + 1) + "\n";
        }
        _nodes += "#\n";
    }

    /**
     * @brief Llegeix les ubicacions
     *          
     *       Per llegir les ubicacions, Obre el fitxer amb el nom a _fitxerUbicacions i el text de cada linia (sense espais)
     *       la va guardant a _ubicacions
     * @pre ---
     * @post S'han llegit les ubicacions i s'han guardat en una estructura de dades
     */
    private void llegirUbicacions() {
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
           
            fitxer = new File(_fitxerUbicacions).getAbsoluteFile();
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);
            String linia;
            while ((linia = br.readLine()) != null) {
                //System.out.println(linia);
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
     * @post S'ha creat un fitxer al directori local amb format TFG 
     * @param nomf Nom del fitxer de sortida
     * @param sol  String a inserir al fitxer
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
     * @brief Retorna el string de nodes
     * @pre ---
     * @post Retorna string _nodes
     */
    public String OptenirNodes(){
        return _nodes;
    }

}
