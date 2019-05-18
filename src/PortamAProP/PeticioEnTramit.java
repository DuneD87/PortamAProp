package PortamAProP;

/**
 * @class SolicitudPendent
 * @brief 
 */

import java.time.LocalTime;

public class PeticioEnTramit {
    
    private final Peticio _instancia; //@brief Ens diu de quina peticio es tracta
    private LocalTime _horaRecollida; //@brief Ens diu l'hora de recollida
    private LocalTime _horaArribada; //@brief Ens diu l'hora d'arribada
    
    /**
     * @brief Ens diu l'estat en que es troba la peticions
     */
    public enum ESTAT {
        ESPERA,
        ENTRANSIT,
        FINALITZADA
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
     * @param instancia Ens diu de quina peticio es tracta
     * @param horaRecollida Ens dona l'hora en que hem recollit els passatgers
     * @param horaArribada  Ens dona l'hora en que hem deixat els passatgers
     */
    public PeticioEnTramit(Peticio instancia, LocalTime horaRecollida, LocalTime horaArribada) {
        _instancia = instancia;
        _horaArribada = horaArribada;
        _horaRecollida = horaRecollida;
    }
    
    /**
     * @brief Ens diu els passatgers que han de pujar
     * @pre ---
     * @post Retorna el numero de passatgers que volen pujar al vehicle
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
     */
    public ESTAT obtenirEstat() {
        return _estat;
    }
}
