package PortamAProP;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


public class Greedy {
    private long LIMIT_FINESTRA_TEMPS;
    private int MAX_DISTANCIA_GREEDY;
    
    public Greedy(long limitTemps,int distanciaMaxima){
        LIMIT_FINESTRA_TEMPS=limitTemps;
        MAX_DISTANCIA_GREEDY=distanciaMaxima;     
    }
    
    public void crearRuta(Vehicle v, ArrayList<Ruta> _rutes, SortedSet<Solicitud> _solicituds, Graph _graf,LlegirFitxerGraf mapa) {
       //System.out.println("###");
        TreeSet<Solicitud> ruta = new TreeSet<Solicitud>();
        Solicitud s = solicitudMesProperaDisponible(v,_solicituds,_graf); //Busquem la solicitud mes propera al vehicle
        int contadorSolicituds = 1;
        while (s != null&& contadorSolicituds < _solicituds.size() + 1) { // mentre quedin solicituds i el numero de solicituds que hem mirat no sigui mes gran que el numero de solicituds totals
            //System.out.println("*******************************");
            //System.out.println("Contador solicituds: " + contadorSolicituds);
            //System.out.println("Mida solicituds: " + _solicituds.size());
            
            if (vehiclePotAssolirSolicitud(v, s,_graf) && DinsFinestraTemps(v, s, ruta)) {// si el vehicle pot assolir la solictud i esta dins la finstra de temps
                ruta.add(s);//afagim la solicitud dintre de la llista de solicituds de la ruta
                s.setEstat(Solicitud.ESTAT.ENTRANSIT);// i posem la solicitud entransit per no tornarla a seleccionar mes tard
              //  System.out.println("Solicitud entrada correctament \n");
            } else {
                s.setEstat(Solicitud.ESTAT.VISITADA); // si la solicitud no ha estat acceptada, la posem com a visitada per no tornarla a selccionar mes tard
                // System.out.println("Solicitud visitada \n");
            }
            contadorSolicituds++; // em mirat 1 solicitud
            s = solicitudMesProperaDisponible(v,_solicituds,_graf);// tornem a seleccionar la solicitud mes propera
        }
        v.setHoraPrimeraSol(null); // "Netejem" el vehicle ja que em acabat aqeulla ruta
        int[] conversio = new int[200];
        Graph sub = crearSubGraf(v, ruta, conversio,_graf,mapa);
        if (ruta.size() == 0) {
            //System.out.println("\nNo hi ha solicituds per crear una ruta\n");
        } else {
            _rutes.add(new Ruta(v, ruta, sub, conversio));
        }

        for (Solicitud sol : _solicituds) {//"Netejem" les solicitds per les proximes rutes
            if (sol.getEstat() == Solicitud.ESTAT.VISITADA) {
                sol.setEstat(Solicitud.ESTAT.ESPERA);
            }
        }

    }
     public boolean DinsFinestraTemps(Vehicle v, Solicitud s, TreeSet<Solicitud> r) {
        boolean valid = false;
       // System.out.println("\n Solicitud dins de finestra de temps:");
        //System.out.println(s);
        if (r.size() == 0) {
            v.setHoraPrimeraSol(s.Emisio());
            v.setHoraUltimaSol(s.Emisio());
            valid = true;
          // System.out.println("Primera solicitud");
        } else {
            
            LocalTime limit = v.getHoraPrimeraSol();
            limit = limit.plusMinutes(LIMIT_FINESTRA_TEMPS);
          //  System.out.println(LIMIT_FINESTRA_TEMPS);
          //  System.out.println(limit);
          // System.out.println("emesio abans de limit: " + s.Emisio().isBefore(limit) );
          // System.out.println("emesio despres del primer del vehicle: " + s.Emisio().isAfter(v.getHoraPrimeraSol()) );
            if (s.Emisio().isBefore(limit) && s.Emisio().isAfter(v.getHoraPrimeraSol()) && s.Emisio().isAfter(v.getHoraUltimaSol())) {
                valid = true;
                //System.out.println("Dins de finestra de temps");
            } else {
               // System.out.println("Fora finsetra de temps \n");
            }
        }
        return valid;
    }
     
     
     
    /**
     * @brief Diu si el vehicle pot anar a la solicitud, fer la solicitud, i
     * tornar al Depot mes proper
     * @pre ---
     * @post Retorna cert si el vehicle v pot assolir la solicitud s
     */
    public boolean vehiclePotAssolirSolicitud(Vehicle v, Solicitud s,Graph _graf) {
        boolean valid = false;
        double anar_solicitud;
        //System.out.println("==============VEHICLE POT =========================\nPot assolir soliciutd? \n" + s.toString() + "\n");
        if (v.nodeInicial() == s.Origen()) {//Si l'origen de la solicitud es on esta el vehicle, no s'haura de desplasar
          //  System.out.println("Node inicial es origen de la solictud");
            anar_solicitud = 0;

        } else {//si no coincidex es calcula el pes fins anar a l'origen de la solicitud
            anar_solicitud = _graf.getNode(v.nodeInicial()).getEdgeBetween(s.Origen()).getAttribute("Pes");
        }
        double completar_solicitud = _graf.getNode(s.Origen()).getEdgeBetween(s.Desti()).getAttribute("Pes");// es calcula el pes de assolir la solicitud
        double depot_proxim = buscarDepotMesProxim(s.Desti(),_graf);//es calcula el pes de tornar al depot mes proper
        double autonomia = v.carregaRestant();
        boolean resultat = anar_solicitud + completar_solicitud + depot_proxim < autonomia;
        boolean pass = s.NumPassatgers() <= v.nPassTotal();
        //System.out.println("Anar solicitud: " + anar_solicitud);
        //System.out.println("completar solicitud: " + completar_solicitud);
        //System.out.println("depot proxim: " + depot_proxim);
        //System.out.println("autonomia: " + autonomia);
        //System.out.println("Resultat:" + resultat);
        //System.out.println("Passatgers solcitud:" + s.NumPassatgers());
        //System.out.println("Passatgers vehicle:" + v.nPassTotal());
        //System.out.println("Passatgers:" + pass);
        if (anar_solicitud + completar_solicitud + depot_proxim < autonomia && s.NumPassatgers() <= v.nPassTotal()) {// si pot assolir la solicitud tant per autonomia com per nombre de passatgers
            valid = true;
            v.descarga(anar_solicitud + completar_solicitud + depot_proxim);
            v.setPosicio(s.Desti());
        }
        //System.out.println("=================================");
        return valid;
    }
    
    
     public Solicitud solicitudMesProperaDisponible(Vehicle v, SortedSet<Solicitud> _solicituds, Graph _graf ) {
        Solicitud millorsol = null;
        boolean trobat = false;
        double millorPes = 0;
        Iterator<Solicitud> it = _solicituds.iterator();
         //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n Buscar solicitud mes propera\n");
        while (!trobat && it.hasNext()) {//mentra quedin solicituds i no hem trobat la millor
            Solicitud ss = it.next();
            //System.out.println("Bucle solicitud\n" + ss.toString());
            if (v.getPosicio() == ss.Origen()) {// si origen de la solicitud esta al mateix node que vehicle no fa falta que mirem mes
                if (ss.getEstat() == Solicitud.ESTAT.ESPERA) {
                    trobat = true;
                    millorsol = ss;
                    //System.out.println("Origen solicitud al nodel del vhehicle");
                }

            } else {//sino mirem per cada solicitud el seu pes i ens quedem amb el de pes mes petit
               // System.out.println("Node on esta el vehicle " + v.getPosicio());
               // System.out.println("Node Origen solicitud " + ss.Origen());
               // System.out.println("Aresta " +  _graf.getNode(v.getPosicio()).getEdgeBetween(ss.Origen()) );
                double pes = _graf.getNode(v.getPosicio()).getEdgeBetween(ss.Origen()).getAttribute("Pes");

                //System.out.println("Pes entra la aresta " + pes);
                if (pes < MAX_DISTANCIA_GREEDY && ss.getEstat() == Solicitud.ESTAT.ESPERA) {
                    if (millorsol == null) {
                        millorsol = ss;
                        millorPes = pes;
                       // System.out.println("primer");
                    } else if (pes < millorPes) {
                        millorsol = ss;
                        millorPes = pes;
                        //System.out.println("millor");
                    }

                }
            }
        }
        if (millorsol != null) {
            //System.out.println("Solicitud mes propera\n" + millorsol.toString());
        } else {
           // System.out.println("Solicitud no trobada");
        }
        return millorsol;
    }
     
     
     public double buscarDepotMesProxim(int index,Graph _graf) {
        double distancia = Integer.MAX_VALUE;
        int numDepots = 0;
        Iterator<Node> it = _graf.iterator();
        while (it.hasNext()) {//primer miro el numero de Depts que hi ha
            Node n = it.next();
            if (n.getAttribute("Tipus").equals("Depot")) {
                numDepots++;
            }

        }

        for (int i = 0; i < numDepots; i++) {//com els son sempre els primers nomes miro els numDepots primers
            if (index == i) { // si el depot es el que estic la distancia =0
                distancia = 0;
            } else {// si no miro la distancia amb tots els depots i em quedo el pes minim
                double pes = _graf.getNode(index).getEdgeBetween(i).getAttribute("Pes");
                if (pes < distancia) {
                    distancia = pes;
                }
            }
        }

        return distancia;
    }
     
     
     
      public Graph crearSubGraf(Vehicle v, TreeSet<Solicitud> llista_solicituds, int[] c,Graph _graf,LlegirFitxerGraf mapa) {
        Iterator<Solicitud> it = llista_solicituds.iterator();
        Graph subgraf = new SingleGraph("Ruta");
        //Primer afegim tots els depots
        for (int x = 0; x < mapa.GetNumDepot(); x++) {
            subgraf.addNode(_graf.getNode(x).getId());
            subgraf.getNode(_graf.getNode(x).getId()).setAttribute("ui.label", "Depot" + x);
            subgraf.getNode(_graf.getNode(x).getId()).addAttribute("Tipus", "Depot");
            subgraf.getNode(_graf.getNode(x).getId()).addAttribute("Nom", _graf.getNode(x).getAttribute("Nom").toString());
            c[_graf.getNode(x).getIndex()] = x;

        }
        while (it.hasNext()) {
            Solicitud s = it.next();
            int o = s.Origen();
            int d = s.Desti();
            String origen = Integer.toString(o);
            String desti = Integer.toString(d);
            if (subgraf.getNode(origen) == null) {
                subgraf.addNode(origen);
                subgraf.getNode(origen).setAttribute("ui.label", origen);
                subgraf.getNode(_graf.getNode(origen).getId()).addAttribute("Nom", _graf.getNode(origen).getAttribute("Nom").toString());
                c[_graf.getNode(origen).getIndex()] = subgraf.getNodeCount() - 1;

            }
            if (subgraf.getNode(desti) == null) {
                subgraf.addNode(desti);
                subgraf.getNode(_graf.getNode(desti).getId()).addAttribute("Nom", _graf.getNode(desti).getAttribute("Nom").toString());
                subgraf.getNode(desti).setAttribute("ui.label", desti);
                c[_graf.getNode(desti).getIndex()] = subgraf.getNodeCount() - 1;
            }

        }
        for (Node n : subgraf) {
            for (Node m : subgraf) {
                if (!n.hasEdgeBetween(m) && !n.equals(m)) {
                    Double pes = _graf.getNode(n.getId()).getEdgeBetween(m.getId()).getAttribute("Pes");
                    subgraf.addEdge(Integer.toString(subgraf.getEdgeCount()), n, m);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount() - 1)).addAttribute("pes", pes);
                    subgraf.getEdge(Integer.toString(subgraf.getEdgeCount() - 1)).addAttribute("ui.label", pes);
                }
            }
        }
        subgraf = mapa.CompletarGraf(subgraf);
        //subgraf.display();
        return subgraf;
    }

}
