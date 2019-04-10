package PortamAProP;

/**
 * @brief Objecte encarregat de construir el nostre graf
 * @author Xavier Avivar & Buenaventura Martinez
 */


import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;
import java.io.*;
import java.util.Scanner;

public class LlegirFitxerGraf {
    private Graph _graf; // @brief Atribut necessari per construir el graf
    
    /**
     * @brief Inicialitza el graf
     * @pre Cadena de caracters amb el format implicit
     * @post Inicialitza la construccio del graf a traves d'una cadena de caracters
     */
    public void initText(Graph graf, String text) {
      
        Scanner scanner = new Scanner(text);
        String linia = scanner.nextLine();
        while (scanner.hasNextLine() && !linia.equals("#")) {
            CrearNode(linia, graf);
        }

        while (scanner.hasNextLine() && !linia.equals("#")) {
            DefinirAresta(linia, graf.getEdgeCount(), graf);

        }
        CompletarGraf(graf);
    }
    
    /**
     * @brief Inicialitza el graf
     * @pre ---
     * @post Inicialitza la construccio del graf a traves d'un fitxer
     */
    public void initFitxer(Graph _graf) {
        //_graf.display();
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            Scanner teclat = new Scanner(System.in);
            System.out.println("Nom del fitxer:");
            String nom = teclat.nextLine();
            fitxer = new File(nom);
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);
            String linia;
            while ((linia = br.readLine()) != null && !linia.equals("#")) {
                CrearNode(linia, _graf);
            }

            while ((linia = br.readLine()) != null && !linia.equals("#")) {
                DefinirAresta(linia, _graf.getEdgeCount(), _graf);

            }
            CompletarGraf(_graf);
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
     * @brief Crea un node
     * @pre s valid per la creació del node
     * @post S'ha construit un nou node 
     */
    private void CrearNode(String s, Graph _graf) {
        String[] parts = s.split(" ");
        _graf.addNode(parts[0]);
        _graf.getNode(parts[0]).addAttribute("Nom", parts[1]);
        _graf.getNode(parts[0]).setAttribute("ui.label", parts[1]);
    }
    //Pre: s valid per la cracio de arestes
    //Post: Crea la aresta segons la configuracio de s
    
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
            _graf.getEdge(Integer.toString(index)).setAttribute("Pes", parts[2]);
            _graf.getEdge(Integer.toString(index)).setAttribute("ui.label", parts[2]);
        }

    }
    
    /**
     * @brief Completa el graf
     * @pre ---
     * @post S'ha completat el graf afegint les arestes que falten fen servir l'algorisme de Dijkstra per trobar el cami minim entre dos nodes
     */
    private void CompletarGraf(Graph _graf) {
        Dijkstra di = new Dijkstra(Dijkstra.Element.EDGE, "Minim", "Pes");
        di.init(_graf);
        for (Node n : _graf) {
            di.setSource(_graf.getNode(n.getId()));
            di.compute();
            for (Node m : _graf) {
                if (!n.getId().equals(m.getId()) && !n.hasEdgeFrom(m.getId())) {
                    _graf.addEdge(String.valueOf(_graf.getEdgeCount()), n, m);
                    _graf.getEdge(String.valueOf(_graf.getEdgeCount() - 1)).addAttribute("Pes", di.getPathLength(m));
                    _graf.getEdge(String.valueOf(_graf.getEdgeCount() - 1)).addAttribute("ui.label", di.getPathLength(m));

                }
            }

        }
        this._graf = _graf;
    }

    /**
     * @brief Ens dona el graf construit
     * @pre S'ha cridat previament a initText o initFitxer
     * @post Retorna un graf complet
     */
    public Graph obtGraph() {
        return _graf;
    }
}
