package PortamAProP;
/**
 * @class SolucionadorRuta
 * @brief Algoritme recursiu que s'encarrega de trobar la millor solucio.
 * @author Xavier Avivar & Buenaventura Martinez
 * @TODO Adaptar l'algoritme perque retorni la millor solucio, comencem per fer que retorni una unica solucio
 */
public class SolucionadorRuta {
    
    private SolucioRuta _actual;
    private SolucioRuta _optim;
    boolean _trobat;
    
    /**
     * @brief Algoritme de backtracking
     * @pre ---
     * @post Busca una unica solucio al problema, plega quan la trobat
     */
    private void backtracking() {
        CandidatRuta iCan = _actual.iniCan();
        while (!iCan.esFi() && !_trobat) {
            if (_actual.acceptable(iCan)) {
                if (!_actual.completa(iCan)) {
                    backtracking();
                    if (!_trobat)
                        _actual.desanotar();
                } else {
                    _trobat = true;
                }
                iCan.seguent();
            }
        }
    }
    
    /**
     * @brief Constructor
     * @param inicial Solucio inicial amb la q treballem
     */
    public SolucionadorRuta(SolucioRuta inicial) {
        _trobat = false;
    }
    
    /**
     * @brief Ens dona la solucio trobada
     * @return SolucioRuta, objecte que conte la solucio potencial al algoritme
     */
    public SolucioRuta obtSolucio() {
        return _actual;
    }
    
    /**
     * @brief Ens diu si existeix solucio
     * @param sol SolucioRuta, objecte que conte la solucio potencial al algoritme
     * @return 
     */
    public boolean existeixSolucio(SolucioRuta sol) {
        _actual = sol;
        backtracking();
        
        return _trobat;
    }
}
