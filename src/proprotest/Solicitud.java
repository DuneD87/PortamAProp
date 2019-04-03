
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

    public Solicitud(int id, int origen, int desti, Time emisio, Time arribada, int numPersones) {
        _identificadorSol = id;
        _llocOrigen = origen;
        _llocDesti = desti;
        _horaEmisio = emisio;
        _horaArribada = arribada;
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
}
