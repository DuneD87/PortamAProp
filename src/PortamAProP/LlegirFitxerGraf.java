package PortamAProP;


import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;
import java.io.*;
import java.util.Scanner;

public class LlegirFitxerGraf {


    public Graph init( Graph _graf) {
        //_graf.display();
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            Scanner teclat = new Scanner(System.in);
            System.out.println("Nom del fitxer:");
            String nom=teclat.nextLine();
            fitxer = new File(nom);
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);
            String linia;
            while ((linia = br.readLine()) != null && !linia.equals("#")) {
                CrearNode(linia,_graf);
            }

            while ((linia = br.readLine()) != null && !linia.equals("#")) {
                DefinirAresta(linia, _graf.getEdgeCount(),_graf);
               
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
        return _graf;
    }
    //Pre: s valid per la creació del node
    //Post: Crea el node en el graf 

    public void CrearNode(String s,Graph _graf) {
        String[] parts = s.split(" ");
        _graf.addNode(parts[0]);
        _graf.getNode(parts[0]).addAttribute("Nom", parts[1]);
        _graf.getNode(parts[0]).setAttribute("ui.label", parts[1]);
    }
    //Pre: s valid per la cracio de arestes
    //Post: Crea la aresta segons la configuracio de s

    public void DefinirAresta(String s, int index,Graph _graf) {
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
    //Pre:--
    //Post: Completa el graf afagin le arestes que falten amb pes del cami més curt

    public void CompletarGraf(Graph _graf) {
        Dijkstra di = new Dijkstra(Dijkstra.Element.EDGE, "Minim", "Pes");
        di.init(_graf);
        for (Node n : _graf) {
            di.setSource(_graf.getNode(n.getId()));
            di.compute();
            for (Node m : _graf) {
                if (!n.getId().equals(m.getId()) && !n.hasEdgeFrom(m.getId())) {
                    _graf.addEdge(String.valueOf(_graf.getEdgeCount()), n, m);
                    _graf.getEdge(String.valueOf(_graf.getEdgeCount()-1)).addAttribute("Pes", di.getPathLength(m));
                    _graf.getEdge(String.valueOf(_graf.getEdgeCount()-1)).addAttribute("ui.label", di.getPathLength(m));

                }
            }

        }
    }

}
