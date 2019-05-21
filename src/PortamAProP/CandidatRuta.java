package PortamAProP;

/**
 * @class CandidatRuta
 * @brief Els nostres candidats son els nodes del graf, i els cataloguem en 3
 * tipus:\n
 * 
 * Origen: Origen d'una peticio.\n
 * Desti: Desti d'una peticio.\n
 * Depot: Punt de carrega.\n
 * 
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class CandidatRuta {
    
    private int _max; //!<@brief Determina el nombre maxim de nodes
    private int _iCan; //!<@brief Candidat actual
    
    /**
     * @brief Constructor
     * @param max ens diu el nombre de candidats a atendre
     */
    public CandidatRuta(int max) {
        _max = max;
        _iCan = 0;
    }
    
    /**
     * @brief Candidat actual
     * @pre ---
     * @post Ens diu la posicio del candidat actual
     */
    public int actual() {
        return _iCan;
    }
    
    /**
     * @brief Es fi 
     * @pre ---
     * @post Ens diu si hem arribat al final de la llista
     */
    public boolean esFi() {
        return _iCan >= _max ;
    }
    
    /**
     * @brief Seguent candidat
     * @pre ---
     * @post Incrementa el candidat en 1
     */
    public void seguent() {
        _iCan++;
    }
}
