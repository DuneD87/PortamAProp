package PortamAProP;

/**
 * @brief Objecte encarregat de construir el nostre graf
 * @author Xavier Avivar & Buenaventura Martinez
 */


import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;
import java.io.*;
import java.lang.String;
import java.util.Scanner;
import java.util.Stack;

public class LlegirFitxerGraf {
    private Graph _graf; // @brief Atribut necessari per construir el graf 
    private int Num_depots;// @brief Atribut necessari per optimitzar temps per fer el greedy
    
    public LlegirFitxerGraf(){
        Num_depots=0;
    }
    
    /**
     * @brief Inicialitza el graf
     * @pre Cadena de caracters amb el format implicit
     * @post Inicialitza la construccio del graf a traves d'una cadena de caracters
     */
    public Graph ModificarGrafPerString(Graph graf, String text, String format) {
      
        String[] lines = text.split("\r\n|\r|\n");
        int contador=0;
        for (int i=contador; i < lines.length && !lines[i].equals("#"); i++){
            //System.out.println(lines[i]);
            CrearNode(lines[i], graf, format);
            contador=i;
        }
            contador+=2;
         for (int i=contador; i < lines.length && !lines[i].equals("#"); i++){
            // System.out.println(lines[i]);
             DefinirAresta(lines[i], graf.getEdgeCount(), graf);

        }
        
        graf=CompletarGraf(graf);
        EliminarNodesDesconectats(graf);
        this._graf = graf;
        return graf;
    }
    
    /**
     * @brief Inicialitza el graf
     * @pre ---
     * @post Inicialitza la construccio del graf a traves d'un fitxer
     */
    public Graph ModificarGrafPerFitxer (Graph _graf, String file, String format) {
        //_graf.display();
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            //Scanner teclat = new Scanner(System.in);
            //System.out.println("Nom del fitxer de nodes:");
            //String nom = teclat.nextLine();
            fitxer = new File(file).getAbsoluteFile();
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);
            String linia;
            while ((linia = br.readLine()) != null && !linia.equals("*")) {
                if(linia.charAt(0)!='#')
                    CrearNode(linia, _graf, format);
            }
            
            while ((linia = br.readLine()) != null && !linia.equals("*")) {
                if(linia.charAt(0)!='#')
                    DefinirAresta(linia, _graf.getEdgeCount(), _graf);

            }
            _graf=CompletarGraf(_graf);
            EliminarNodesDesconectats(_graf);
            
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
        this._graf = _graf;
        return _graf;
    }
    
    /**
     * @brief Crea un node
     * @pre s valid per la creació del node
     * @post S'ha construit un nou node 
     */
    private void CrearNode(String s, Graph _graf, String format) {
        String[] parts = s.split(" ");
        _graf.addNode(parts[0]);
        _graf.getNode(parts[0]).addAttribute("Nom", parts[1]);
        _graf.getNode(parts[0]).setAttribute("ui.label", parts[1]);
        if(format.equals("Depot")){
             _graf.getNode(parts[0]).addAttribute("Tipus", "Depot");
             Num_depots++;
             _graf.getNode(parts[0]).addAttribute("VehiclesMaxim", parts[2]);
             _graf.getNode(parts[0]).addAttribute("VehiclesActual", "0");
        }else
             _graf.getNode(parts[0]).addAttribute("Tipus", "peticions");
    }
    
    /**
     * @brief Defineix arestes
     * @pre s valid per la cracio de arestes
     * @post Crea la aresta segons la configuracio de s
     */
    private void DefinirAresta(String s, int index, Graph _graf) {
        String[] parts = s.split(" ");
        /*
         graf.addEdge(parts[3], parts[0], parts[1]);
         graf.getEdge(parts[3]).setAttribute("Pes", parts[2]);
         graf.getEdge(parts[3]).setAttribute("ui.label", parts[2]);
         */
        if (!_graf.getNode(parts[0]).hasEdgeToward(parts[1])) {
            _graf.addEdge(Integer.toString(index), parts[0], parts[1]);
            _graf.getEdge(Integer.toString(index)).setAttribute("Pes", Double.parseDouble(parts[2]));
            _graf.getEdge(Integer.toString(index)).setAttribute("ui.label", parts[2]);
        }

    }
    
    /**
     * @brief Completa el graf
     * @pre ---
     * @post S'ha completat el graf afegint les arestes que falten fen servir l'algorisme de Dijkstra per trobar el cami minim entre dos nodes
     */
    public Graph CompletarGraf(Graph graf) {
        Dijkstra di = new Dijkstra(Dijkstra.Element.EDGE, "Minim", "Pes");
        di.init(graf);
        for (Node n : graf) {
            di.setSource(graf.getNode(n.getId()));
            di.compute();
            for (Node m : graf) {
                if (!n.getId().equals(m.getId()) && !n.hasEdgeFrom(m.getId())) {
                    graf.addEdge(String.valueOf(graf.getEdgeCount()), n, m);
                    graf.getEdge(String.valueOf(graf.getEdgeCount() - 1)).addAttribute("Pes", di.getPathLength(m));
                    graf.getEdge(String.valueOf(graf.getEdgeCount() - 1)).addAttribute("ui.label", di.getPathLength(m));

                }
            }

        }
        return graf;
    }

    /**
     * @brief Ens dona el graf construit
     * @pre S'ha cridat previament a initText o initFitxer
     * @post Retorna un graf complet
     */
    public Graph obtGraph() {
        return _graf;
    }

    
    /**
     * @brief Elimina els nodes desconectats o conectats nomes entre dos nodes
     * @pre graf complet amb pessos
     * @post Retorna un graf complet sense desconxions
     */
    public void EliminarNodesDesconectats(Graph graf) {
        int contador;
        Stack<Node> pila = new Stack<Node>();
        for (Node n : graf) {
            contador = 0;
            //System.out.println(n.getId());
            for (Node m : graf) {
                if (n.hasEdgeFrom(m.getId())) {
                    Object valor = n.getEdgeBetween(m).getAttribute("Pes");
                    if (valor.toString().equals("Infinity")) {
                        contador++;
                    }

                }
            }
            if (contador == graf.getNodeCount() - 1 || contador == graf.getNodeCount() - 2) {
                //System.out.println(n.getAttribute("Nom").toString());

                pila.push(n);
            }
        }
        while (!pila.empty()) {
            graf.removeNode(pila.pop());
        }
        this._graf = graf;
    }
    /**
     * @brief Retorna el numero de depots del graf
     * @pre Cert
     * @post Retorna el numero de depots
     */
    public int GetNumDepot(){
        return Num_depots;
    }
}
