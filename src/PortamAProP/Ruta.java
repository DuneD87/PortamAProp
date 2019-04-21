package PortamAProP;

import java.util.TreeSet;
import org.graphstream.graph.*;

public class Ruta {
    private Vehicle _vehicle;
    private TreeSet<Solicitud> _solicituds;
    private Graph _graf;
    
    public Ruta(Vehicle vehicle,TreeSet<Solicitud> sol,Graph g){
        _vehicle=vehicle;
        _solicituds=sol;
        _graf=g;
    }
    
    
    
}
