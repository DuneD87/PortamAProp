package PortamAProP;

/**
 * @brief Classe encarregada de guardar informacio sobre les peticions
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.sql.Time;
import java.time.LocalTime;
import org.graphstream.graph.*;
import org.graphstream.graph.Node;



public class Peticio implements Comparable<Peticio> {

    private int _identificadorSol; //@brief Identificador de peticio
    private int _llocOrigen; //@brief Origen de la peticio
    private int _llocDesti; //@brief Desti de la peticio
    private LocalTime _horaEmisio; //@brief Hora, minuts i segons en la que s'ha fet la peticio
    private LocalTime _horaArribada; //@brief Hora, minuts i segons en que s'ha acabat el trajecte
    private LocalTime _horaRecollida;//@brief Hora real en que recollim els clients
    private int _numPassatgers; //@brief Numero de passatgers de la peticio
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
     * @post S'ha construit una nova peticio amb els parametres donats
     */
    public Peticio(int id, int origen, int desti, LocalTime emisio, int numPersones) {
        _identificadorSol = id;
        _llocOrigen = origen;
        _llocDesti = desti;
        _horaEmisio = emisio;
        _horaArribada=null;
        _horaRecollida = null;
        _numPassatgers = numPersones;
        _estat = ESTAT.ESPERA;
    }
    
    public Peticio(Peticio sol) {
        _identificadorSol = sol._identificadorSol;
        _llocOrigen = sol._llocOrigen;
        _llocDesti = sol._llocDesti;
        _horaEmisio = sol._horaEmisio;
        _estat = sol._estat;
        _horaRecollida = sol._horaRecollida;
        _horaArribada = sol._horaArribada;
        _numPassatgers = sol._numPassatgers;
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
     * @pre S'ha inicialitzat la peticio
     * @post Ens diu l'identificador de la peticio
     */
    public int identificador(){
        return _identificadorSol;
    }
   
    /**
     * @brief Ens diu l'origen
     * @pre S'ha inicialtizat la peticio
     * @post Ens diu l'ID del node origen
     */
    public int origen(){
        return _llocOrigen;
    }
    
    /**
     * @brief Ens diu el desti
     * @pre S'ha inicialitzat la peticio
     * @post Ens diu l'ID del node desti
     */
    public int desti(){
        return _llocDesti;
    }
    
    /**
     * @brief Ens diu l'hora d'emissio
     * @pre S'ha inicialitzat la peticio
     * @return 
     */
    public LocalTime emissio(){
        return _horaEmisio;
    }
    
    /**
     * @brief Ens diu l'hora d'arribada
     * @pre S'ha finalitzat la peticio
     * @post Ens diu l'hora d'arribada en format Time 'HH:MM:SS'
     */
    public LocalTime arribada(){
        return _horaArribada;
    }
    
    /**
     * @brief Ens diu el numero de passatgers
     * @pre ---
     * @post Retorna un enter que ens diu el numero de passatges actualment al vehicle
     */
    public int numPassatgers(){
        return _numPassatgers;
    }
    
    /**
     * @brief Assigna l'hora d'arribada
     * @pre Hora valida
     * @post S'ha completat la peticio amb hora d'arribada 'arribada'
     */
    public void assignarHoraArribada(int minut){
        _horaArribada = _horaRecollida.plusMinutes(minut);
    }
    
    /**
     * @brief Assigna l'hora real de recollida
     * @pre ---
     * @post S'ha assignat una hora de recollida real
     */
    public void assignarHoraRecollida(int minut) {
        
        _horaRecollida = _horaEmisio.plusMinutes(minut);
    }
    
    public LocalTime obtenirHoraRecollida(){
        return _horaRecollida;
    }
    /**
     * @brief Sobrecarrega del metode compareTo
     * @pre ---
     * @post Ens diu si la peticio donada es diferent del que tenim (mes petit si el valor es negatiu, mes gran si el valor es positiu, 0 si tenen el mateix valor)
     */
    @Override
    public int compareTo(Peticio o) {
        return _horaEmisio.compareTo(o._horaEmisio);
    }
    
    /**
     * @brief Sobrecarrega del metode equals
     * @pre ---
     * @post Ens diu si la peticio donada es igual a la que tenim
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true; // Es la mateixa referencia
        if (!(obj instanceof Peticio)) return false; //L'objecte no pertany a la classe Naip
        Peticio s = (Peticio)obj;//Hem arribat fins aqui, fem casting del objecte a Naip;
        return _horaEmisio.equals(s._horaEmisio) && _identificadorSol == s._identificadorSol; //Tenen el mateix pal i son del mateix tipus ?
    }
    
    /**
     * @brief Ens diu l'estat de la peticio
     * @pre ---
     * @post Ens diu si la peticio es troba en espera, en transit o finalitzada
     */
    public ESTAT obtenirEstat() {
        return _estat;
    }
    
    /**
     * @brief Canvia l'estat de la peticio
     * @pre ---
     * @post S'ha canviat l'estat de la peticio
     */
    public void modificarEstat(ESTAT s) {
        _estat = s;
    }
}
