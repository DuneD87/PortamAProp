package PortamAProP;

/**
 * @brief Classe encarregada de guardar informacio sobre les solicituds
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.sql.Time;
import java.time.LocalTime;
import org.graphstream.graph.*;
import org.graphstream.graph.Node;



public class Solicitud implements Comparable<Solicitud> {

    private int _identificadorSol; //@brief Identificador de solicitud
    private int _llocOrigen; //@brief Origen de la solicitud
    private int _llocDesti; //@brief Desti de la solicitud
    private LocalTime _horaEmisio; //@brief Hora, minuts i segons en la que s'ha fet la solicitud
    private LocalTime _horaArribada; //@brief Hora, minuts i segons en que s'ha acabat el trajecte
    private LocalTime _horaRecollida;//@brief Hora real en que recollim els clients
    private int _numPassatgers; //@brief Numero de passatgers de la solicitud
    enum ESTAT {
        ESPERA,
        VISITADA,
        ENTRANSIT,
        FINALITZADA
    }
    private ESTAT _estat;
    /**
     * @brief Constructor
     * @pre ---
     * @post S'ha construit una nova solicitud amb els parametres donats
     */
    public Solicitud(int id, int origen, int desti, LocalTime emisio, int numPersones) {
        _identificadorSol = id;
        _llocOrigen = origen;
        _llocDesti = desti;
        _horaEmisio = emisio;
        _horaArribada=null;
        _horaRecollida = null;
        _numPassatgers = numPersones;
        _estat = ESTAT.ESPERA;
    }
    
    /**
     * @brief Converteix l'objecte en text
     * @pre ---
     * @post Ens retorna l'objecte en format de cadena de caracters
     */
    @Override
    public String toString() {
        String resultat = "\nIdentificador: " + _identificadorSol + "\n"
                + "LlocOrigen: " + _llocOrigen + "\n"
                + "LLocDesti: " + _llocDesti + "\n"
                + "HoraEmisio: " + _horaEmisio + "\n"
                + "HoraRecollida: " + _horaRecollida + "\n"
                + "HoraArribada: " + _horaArribada + "\n"
                + "NumeroPassatgers: " + _numPassatgers + "\n"
                + "Estat: "            + _estat.toString() + "\n"
                + "=============================================";
        return resultat;
    }
    
    /**
     * @brief ---
     * @pre S'ha inicialitzat la solicitud
     * @post Ens diu l'identificador de la solicitud
     */
    public int Identificador(){
        return _identificadorSol;
    }
   
    /**
     * @brief Ens diu l'origen
     * @pre S'ha inicialtizat la solicitud
     * @post Ens diu l'ID del node origen
     */
    public int Origen(){
        return _llocOrigen;
    }
    
    /**
     * @brief Ens diu el desti
     * @pre S'ha inicialitzat la solicitud
     * @post Ens diu l'ID del node desti
     */
    public int Desti(){
        return _llocDesti;
    }
    
    /**
     * @brief Ens diu l'hora d'emissio
     * @pre S'ha inicialitzat la solicitud
     * @return 
     */
    public LocalTime Emisio(){
        return _horaEmisio;
    }
    
    /**
     * @brief Ens diu l'hora d'arribada
     * @pre S'ha finalitzat la solicitud
     * @post Ens diu l'hora d'arribada en format Time 'HH:MM:SS'
     */
    public LocalTime Arribada(){
        return _horaArribada;
    }
    
    /**
     * @brief Ens diu el numero de passatgers
     * @pre ---
     * @post Retorna un enter que ens diu el numero de passatges actualment al vehicle
     */
    public int NumPassatgers(){
        return _numPassatgers;
    }
    
    /**
     * @brief Assigna l'hora d'arribada
     * @pre Hora valida
     * @post S'ha completat la solicitud amb hora d'arribada 'arribada'
     */
    public void AssignarArribada(LocalTime arribada){
        _horaArribada=arribada;
    }
    
    /**
     * @brief Assigna l'hora real de recollida
     * @pre ---
     * @post S'ha assignat una hora de recollida real
     */
    public void assignarHoraRecollida(LocalTime hora) {
        _horaRecollida = hora;
    }
    
    public LocalTime getRecollida(){
        return _horaRecollida;
    }
    /**
     * @brief Sobrecarrega del metode compareTo
     * @pre ---
     * @post Ens diu si la solicitud donada es diferent del que tenim (mes petit si el valor es negatiu, mes gran si el valor es positiu, 0 si tenen el mateix valor)
     */
    @Override
    public int compareTo(Solicitud o) {
        return _horaEmisio.compareTo(o._horaEmisio);
    }
    
    /**
     * @brief Sobrecarrega del metode equals
     * @pre ---
     * @post Ens diu si la solicitud donada es igual a la que tenim
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true; // Es la mateixa referencia
        if (!(obj instanceof Solicitud)) return false; //L'objecte no pertany a la classe Naip
        Solicitud s = (Solicitud)obj;//Hem arribat fins aqui, fem casting del objecte a Naip;
        return _horaEmisio.equals(s._horaEmisio) && _identificadorSol == s._identificadorSol; //Tenen el mateix pal i son del mateix tipus ?
    }
    
    /**
     * @brief Ens diu l'estat de la solicitud
     * @pre ---
     * @post Ens diu si la solicitud es troba en espera, en transit o finalitzada
     */
    public ESTAT getEstat() {
        return _estat;
    }
    
    /**
     * @brief Canvia l'estat de la solicitud
     * @pre ---
     * @post S'ha canviat l'estat de la solicitud
     */
    public void setEstat(ESTAT s) {
        _estat = s;
    }
}
