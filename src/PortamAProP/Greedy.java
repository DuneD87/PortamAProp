package PortamAProP;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * @class Greedy
 * @brief Classe que a partir de un vehicle i una llista de peticions genera una ruta que el vehicle pot assolir amb la seva autonimia sense haver de anar a carregar 
 * -Donat 2 numeros, determinam:
 * --limitTemps: Ens diu els minuts maxim que el vehicle podra assolir peticions desde la primera solicitud
 * --distanciaMaxima: Ens diu la distancia(en minuts) que el vehicle pot buscar peticions
 * 
 * @author Xavier Avivar & Buenaventura Martinez
 */
public class Greedy {
    private long LIMIT_FINESTRA_TEMPS;
    private int MAX_DISTANCIA_GREEDY;
    
    /**
     * @brief Constructor de la classe Gready
     * @param limitTemps  Ens diu els minuts maxim que el vehicle podra assolir peticions desde la primera solicitud
     * @param distanciaMaxima Ens diu la distancia(en minuts) que el vehicle pot buscar peticions
     */
    public Greedy(long limitTemps,int distanciaMaxima){
        LIMIT_FINESTRA_TEMPS=limitTemps;
        MAX_DISTANCIA_GREEDY=distanciaMaxima;     
    }
    
    /**
     * @brief Metode encarregat de generar una ruta per el vehicle v a partir de _solicituds
     * 
     *      Per crear la ruta per el vehicle, el primer es buscar la peticio mes propera al vehicle,
     *      Mentre la solicitud sigui acceptada( !=null) i el contador de solicituds no superi al numero de solicituds,
     *      mirem si el vehicle pot assolir la solicitud tant per autonomia com per finestra de temps, si es aixi afagim la peticio a la ruta, restem el cost del  la  peticio
     *      a l'autonomia i posem el vehicle al desti de la peticio, i modifiquem l'estat de la peticio a ENTRANSIT
     *      si per alguna cosa no es acceptada o per autonomia o per finstra de temps, modifiquem l'estat de la peticio a VISITADA,
     *      sumem 1 al contador de peticions i tornem a buscar la seguent peticio disponible.
     *      Un cop finalitzat el bucle, reseteixo el temps de la primera peticio atesa per el vehicle,
     *      creo el subraf de la ruta, i si la ruta te alguna peticio, la afageixo a la llita de rutes, sino ho ignoro( una ruta sense peticions no te sentit)
     *      Finalment canvio el estat de totes les peticions VISITADES A ESPERA
     * 
     * @post Ruta inserida _rutes correctament 
     */
    public void crearRuta(Vehicle v, ArrayList<Ruta> _rutes, SortedSet<Peticio> _solicituds, Graph _graf,LlegirFitxerGraf mapa) {
        TreeSet<Peticio> ruta = new TreeSet<Peticio>();
        Peticio s = solicitudMesProperaDisponible(v,_solicituds,_graf); //Busquem la peticio mes propera al vehicle
        int contadorSolicituds = 1;
        while (s != null&& contadorSolicituds < _solicituds.size() + 1) { // mentre quedin peticions i el numero de peticions que hem mirat no sigui mes gran que el numero de peticions totals
            double valid=vehiclePotAssolirSolicitud(v, s, _graf);
            if (valid>0 && DinsFinestraTemps(v, s, ruta)) {// si el vehicle pot assolir la solictud i esta dins la finstra de temps
                ruta.add(s);//afagim la peticio dintre de la llista de peticions de la ruta
                v.descarga(valid);
                v.setPosicio(s.desti());
                s.modificarEstat(Peticio.ESTAT.ENTRANSIT);// i posem la peticio entransit per no tornarla a seleccionar mes tard
            } else {
                s.modificarEstat(Peticio.ESTAT.VISITADA); // si la peticio no ha estat acceptada, la posem com a visitada per no tornarla a selccionar mes tard
            }
            contadorSolicituds++; // em mirat 1 peticio
            s = solicitudMesProperaDisponible(v,_solicituds,_graf);// tornem a seleccionar la peticio mes propera
        }
        v.setHoraPrimeraSol(null); // "Netejem" el vehicle ja que em acabat aqeulla ruta
        int[] conversio = new int[200];
        Graph sub = crearSubGraf(v, ruta, conversio,_graf,mapa);
        if (ruta.isEmpty()) {
            
        } else {
            _rutes.add(new Ruta(v, ruta, sub, conversio));
        }

        for (Peticio sol : _solicituds) {//"Netejem" les solicitds per les proximes rutes
            if (sol.obtenirEstat() == Peticio.ESTAT.VISITADA) {
                sol.modificarEstat(Peticio.ESTAT.ESPERA);
            }
        }

    }
    
    /**
     * @brief Metode que ens diu si la peticio esta dins la finestra de temps acceptable pel vehicle
     *      
     *        Per determinar si la peticio esta dins de la finstra de temps del vehicle, el primer que es fa
     *         es mirar si el vehicle te alguna peticio acceptada, sino es aixi, inicialitzem el temps de la primera peticio
     *          acceptada per el vehilce. Si ja hi han peticions, es mira si la peticio s'ha emes despres de la primera que ha acceptat 
     *          el vehicle i abans del limit que accepta el vehicle.
     *        
     * @post Retorna verdades si la peticio esta dins de la finestra de temps acceptable pel vehicle
     * @return Verdades si la peticio esta dins la finestra de temps acceptable pel vehicle
     */
     public boolean DinsFinestraTemps(Vehicle v, Peticio s, TreeSet<Peticio> r) {
        boolean valid = false;
        if (r.isEmpty()) {
            v.setHoraPrimeraSol(s.emissio());
            valid = true;
        } else {
            
            LocalTime limit = v.getHoraPrimeraSol();
            limit = limit.plusMinutes(LIMIT_FINESTRA_TEMPS);
            if (s.emissio().isBefore(limit) && s.emissio().isAfter(v.getHoraPrimeraSol())) {
                valid = true;
            } else {
            }
        }
        return valid;
    }
     
     
     
    /**
     * @brief Metode que ens diu si el vehicle pot assolir la peticio, si pot anar a l'origen de la peticio
     *        si la pot fer i si pot tornar al depot mes proper sense anar a carregar
     * 
     *      Per detrminar si el vehicle pot assolir la peticio calculem les distancies i les sumem, si 
     *      la suma es menor que l'autonomia actual, la peticio es valida.
     * @pre ---
     * @post Retorna cert si el vehicle v pot assolir la peticio s
     */
    public double vehiclePotAssolirSolicitud(Vehicle v, Peticio s,Graph _graf) {
        double valid = -1;
        double anar_solicitud;
        if (v.nodeInicial() == s.origen()) {//Si l'origen de la peticio es on esta el vehicle, no s'haura de desplasar
            anar_solicitud = 0;

        } else {//si no coincidex es calcula el pes fins anar a l'origen de la peticio
            anar_solicitud = _graf.getNode(v.nodeInicial()).getEdgeBetween(s.origen()).getAttribute("Pes");
        }
        double completar_solicitud = _graf.getNode(Integer.toString(s.origen())).getEdgeBetween(Integer.toString(s.desti())).getAttribute("Pes");// es calcula el pes de assolir la peticio
        double depot_proxim = buscarDepotMesProxim(s.desti(),_graf);//es calcula el pes de tornar al depot mes proper
        double autonomia = v.carregaRestant();
        if (anar_solicitud + completar_solicitud + depot_proxim < autonomia && s.numPassatgers() <= v.nPassTotal()) {// si pot assolir la peticio tant per autonomia com per nombre de passatgers
            valid = anar_solicitud + completar_solicitud + depot_proxim;
        }
        return valid;
    }
    
    /**
     *@breif Metode que retorna la peticio disponible mes propera al vehicle
     *      
     *      Per determinar quina es la peticio disponible mes propera busca per totes les solicituds en estat ESPERA:
     *      si troba una que l'inici es el node on esta el vehicle la dona com a valida, 
     *      sino busca per totes les peticios la mes propera dins del rang MAX_DISTNACIA_GREEDY
     *      i la retorna
     * 
     * @post: --
     * @post: Retorna la peticio valida mes propera
     */
    
     public Peticio solicitudMesProperaDisponible(Vehicle v, SortedSet<Peticio> _solicituds, Graph _graf ) {
        Peticio millorsol = null;
        boolean trobat = false;
        double millorPes = 0;
        Iterator<Peticio> it = _solicituds.iterator();
        while (!trobat && it.hasNext()) {//mentra quedin peticions i no hem trobat la millor
            Peticio ss = it.next();
            if (v.getPosicio() == ss.origen()) {// si origen de la peticio esta al mateix node que vehicle no fa falta que mirem mes
                if (ss.obtenirEstat() == Peticio.ESTAT.ESPERA) {
                    trobat = true;
                    millorsol = ss;
                }

            } else {//sino mirem per cada peticio el seu pes i ens quedem amb el de pes mes petit
                double pes = _graf.getNode(v.getPosicio()).getEdgeBetween(ss.origen()).getAttribute("Pes");
                if (pes < MAX_DISTANCIA_GREEDY && ss.obtenirEstat() == Peticio.ESTAT.ESPERA) {
                    if (millorsol == null) {
                        millorsol = ss;
                        millorPes = pes;
                    } else if (pes < millorPes) {
                        millorsol = ss;
                        millorPes = pes;
                    }

                }
            }
        }
        return millorsol;
    }
     
     
     /**
      * 
      * @breif Metode que retorna la distancia fins al depot mes proper
      * 
      *     Per determinal la distancia fins al depot mes proper, primer detrmino el numero
      *     de depots que hi ha al graf
      *     Com els depots sempre son els primers al graf, per cada index de 0 fins el numero de depots:
      *     miro la distancia que hi ha respecte el vehicle i em quedo amb el minim i el retorno
      * @pre: Els depots han de ser els primers nodes
      * @post: Retoro la distancia entre el node amb index(index) i el depot mes proper
      */
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
     /**
      * 
      * @brief Metode que crea i retorna un graf a partir de una llista de peticions 
      * 
      *     Per crer un subgraf a partr de la llista de peticions, primer creo el graph i li afageixo tots els depots, 
      *     despres per cada peticio afageixo al graf el origen i el desti.
      *     Per detrminar els pesos de les arestes del subgraf, i copia el pes del graf complet al subgraf
      * 
      * @post: Retorna el subgraf creat a partir de la llista de peticions
      */
     
     
      public Graph crearSubGraf(Vehicle v, TreeSet<Peticio> llista_solicituds, int[] c,Graph _graf,LlegirFitxerGraf mapa) {
        Iterator<Peticio> it = llista_solicituds.iterator();
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
            Peticio s = it.next();
            int o = s.origen();
            int d = s.desti();
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
        return subgraf;
    }

}
