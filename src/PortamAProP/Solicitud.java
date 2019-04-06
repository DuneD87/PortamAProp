package PortamAProP;

import java.sql.Time;
import org.graphstream.graph.*;
import org.graphstream.graph.Node;

public class Solicitud {

    private int _identificadorSol; //Identificador de solicitud
    private int _llocOrigen; //Origen de la solicitud
    private int _llocDesti; //Desti de la solicitud
    private Time _horaEmisio; // Hora, minuts i segons en la que s'ha fet la solicitud
    private Time _horaArribada; //Hora, minuts i segons en que s'ha acabat el trajecte
    private int _numPassatgers; //Numero de passatgers de la solicitud

    public Solicitud(int id, int origen, int desti, Time emisio, int numPersones) {
        _identificadorSol = id;
        _llocOrigen = origen;
        _llocDesti = desti;
        _horaEmisio = emisio;
        _horaArribada=null;
        _numPassatgers = numPersones;
    }

    public String toString() {
        String resultat = "Identificador: " + _identificadorSol + "\n"
                + "LlocOrigen: " + _llocOrigen + "\n"
                + "LLocDesti: " + _llocDesti + "\n"
                + "HoraEmisio: " + _horaEmisio + "\n"
                + "HoraArribada: " + _horaArribada + "\n"
                + "NumeroPassatgers: " + _numPassatgers + "\n"
                + "=============================================";
        return resultat;
    }
    //Getters
    
    //Pre:
    //Post:
    public int Identificador(){
        return _identificadorSol;
    }
    //Pre:
    //Post:
    public int Origen(){
        return _llocOrigen;
    }
    //Pre:
    //Post:
    public int Desti(){
        return _llocDesti;
    }
    //Pre:
    //Post:
    public Time Emisio(){
        return _horaEmisio;
    }
    //Pre:
    //Post:
    public Time Arribada(){
        return _horaArribada;
    }
    //Pre:
    //Post:
    public int NumPassatgers(){
        return _numPassatgers;
    }
    
    //Setters
    
    //Pre:
    //Post:
    public void AssignarArribada(Time arribada){
        _horaArribada=arribada;
    }
}
