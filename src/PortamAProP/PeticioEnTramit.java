package PortamAProP;

/**
 * @class PeticioEnTramit
 * @brief Objecte compost per una peticio i diferents atributs que ens donen
 * informacio sobre l'estat i hores de les peticions
 * @author Xavier Avivar & Buenaventura Martinez
 */

import java.time.LocalTime;

public class PeticioEnTramit {
    
    private final Peticio _instancia; //!<@brief Ens diu de quina peticio es tracta
    private LocalTime _horaRecollida; //!<@brief Ens diu l'hora de recollida
    private LocalTime _horaArribada; //!<@brief Ens diu l'hora d'arribada
    
    /**
     * @brief Ens diu l'estat en que es troba la peticions
     */
    public enum ESTAT {
        ESPERA,//!<@brief La peticio esta esperan a ser acceptada
        ENTRANSIT,//!<@brief La peticio es troba en transit, els passatgers estan al vehicle
        FINALITZADA//!<@brief La peticio ha estat finalitzada
    }
    private ESTAT _estat;
    
    /**
     * @brief Constructor amb parametre
     * @param sol Ens diu de quina peticio es tracta
     */
    public PeticioEnTramit(Peticio sol) {
        _instancia = sol;
        _horaArribada = null;
        _horaRecollida = null;
        _estat = PeticioEnTramit.ESTAT.ESPERA;
    }
    
    /**
     * @brief Constructor de copia
     * @param peticio Ens dona la PeticioEnTramit anterior i construeix una nova
     */
    public PeticioEnTramit(PeticioEnTramit peticio) {
        _instancia = peticio._instancia;
        _horaArribada = peticio._horaArribada;
        _horaRecollida = peticio._horaRecollida;
        _estat = peticio._estat;
    }
    
    /**
     * @brief Ens diu els passatgers que han de pujar
     * @pre ---
     * @post Retorna el numero de passatgers que volen pujar al vehicle
     * @return numero de passatgers
     */
    public int nPassatgers() {
        return _instancia.numPassatgers();
    }
    
    /**
     * @brief Actualiza l'estat
     * @pre ---
     * @post Actualitza l'estat de la peticio en un dels 3 estats disponibles
     * Estat ESPERA: La peticio esta esperan a ser acceptada
     * Estat ENTRANSIT: La peticio es troba en transit, els passatgers estan al vehicle
     * Estat FINALITZADA: La peticio ha estat finalitzada
     * @param s Ens dona l'estat
     */
    public void actualitzarEstat(ESTAT s) {
        _estat = s;
    }
    
    /**
     * @brief Ens diu l'estat de la peticio
     * @pre ---
     * @post Ens diu l'estat en que es troba la peticio
     * Estat ESPERA: La peticio esta esperan a ser acceptada
     * Estat ENTRANSIT: La peticio es troba en transit, els passatgers estan al vehicle
     * Estat FINALITZADA: La peticio ha estat finalitzada
     * @return Estat actual
     */
    public ESTAT obtenirEstat() {
        return _estat;
    }
    
    /**
     * @brief Hora d'emissio
     * @pre ---
     * @post Ens diu l'hora en que s'ha emes la peticio
     * @return hora d'emissio
     */
    public LocalTime horaEmissio() {
        return _instancia.emissio();
    }
    
    /**
     * @brief Assigna hora recollida
     * @pre Peticio en estat ESPERA
     * @post S'ha assignat l'hora de recollida a la peticio en tramit
     * @param temps Ens diu quant de temps li hem de sumar a l'hora d'emissio
     */
    public void assignarRecollida(int temps) {
        _horaRecollida = _instancia.emissio().plusMinutes(temps);
    }
    
    /**
     * @brief Assigna hora d'arribada
     * @pre Peticio en estat ENTRANSIT i s'ha assignat una hora de recollida previament
     * @post S'ha assignat l'hora d'arribada
     * @param temps Ens diu quant de temps li hem de sumar a l'hora de recollida
     */
    public void assignarArribada(int temps) {
        _horaArribada = _horaRecollida.plusMinutes(temps);
    }
    
    /**
     * @brief Ens dona l'hora de recollida
     * @pre S'ha assignat previament una hora de recollida i ESTAT: ENTRANSIT
     * @post Retorna l'hora de recollida
     * @return Hora de recollida en format LocalTime
     */
    public LocalTime obtenirHoraRecollida() {
        return _horaRecollida;
    }
    
    /**
     * @brief Ens dona l'hora d'arribada
     * @pre S'ha assignat previament una hora d'arribada i ESTAT: FINALITZADA
     * @post Retorna l'hora d'arribada
     * @return Hora d'arribada en format LocalTime
     */
    public LocalTime obtenirHoraArribada() {
        return _horaArribada;
    }
    
    /**
     * @brief Ens diu l'origen de la peticio
     * @pre ---
     * @post Retorna un enter que ens diu l'origen de la peticio
     * @return Enter que representa l'identificador del node
     */
    public int obtenirOrigen() {
        return _instancia.origen();
    }
    
    /**
     * @brief Ens diu el desti de la peticio
     * @pre ---
     * @post Retorna un enter que ens diu el desti de la peticio
     * @return Enter que representa l'identificador del node
     */
    public int obtenirDesti() {
        return _instancia.desti();
    }
    
    /**
     * @brief Metode toString
     * @pre ---
     * @post Ens dona l'objecte en forma de cadena de caracters
     * @return String que conte informacio sobre l'objecte
     */
    public String toString() {
        return "Hora emissio: " + _instancia.emissio() + "\n"
              +"Hora recollida: " + _horaRecollida + "\n"
              +"Hora arribada: " + _horaArribada + "\n"
              +"Estat: " + _estat;
                
    }
}
