package PortamAProP;

/**
 * @class SolucionadorRuta
 * @brief Algoritme recursiu que s'encarrega de trobar la millor solucio.
 * @author Xavier Avivar & Buenaventura Martinez
 */
public class SolucionadorRuta {

    private SolucioRuta _actual;
    private SolucioRuta _optim;
    private boolean _trobat;
    private int _nSolucions;
    private int _nSolucionsTotal;
    
    /**
     * @brief Algoritme de backtracking
     * @pre ---
     * @post Computa la millor solucio possible al problema.Es proven totes
     * les possibles combinacions del conjunt de candidats, i escollim la combinacio
     * amb un pes total menor
     */
    private void backtracking() {
        CandidatRuta iCan = _actual.iniCan();
        while (!iCan.esFi()) {
            if (_actual.acceptable(iCan) && _actual.potSerMillor(_optim)) {
                _actual.anotar(iCan);
                if (!_actual.completa()) {
                    backtracking();
                    
                }
                if (_actual.completa() && !_trobat) { //La primera opcio sempre es millor
                    _optim = new SolucioRuta(_actual);
                    _trobat = true;
                }
                if (_actual.completa() && _actual.esMillor(_optim) && _trobat) { //unicament si hem trobat la primera solucio, procedim a buscar l'optim
                    _optim = new SolucioRuta(_actual);
                    System.out.print(".");
                    System.out.flush();
                    //_trobat = true;
                    _nSolucions++;
                    if (_nSolucions < (_nSolucionsTotal + 100))
                        System.out.print(".");
                    else {
                        _nSolucionsTotal = _nSolucions;
                        System.out.println("SOLUCIONS TROBADES: " + _nSolucionsTotal);
                    }
                        
                }
                
                 _actual.desanotar(iCan);

            }
            iCan.seguent();
        }
    }
    
    /**
     * @brief Constructor
     * @param inicial Solucio inicial amb la q treballem
     */
    public SolucionadorRuta(SolucioRuta inicial) {
        _actual = inicial;
        _optim = new SolucioRuta(_actual);
        _trobat = false;
        _nSolucions = 0;
        _nSolucionsTotal = 0;
    }

    /**
     * @brief Ens diu si existeix solucio
     * @post Executa l'algoritme recursiu de backtracking per determinar si el
     * problema te solucio, en cas de trobar solucio, finalitza la ruta que ha 
     * efectuat el vehicle.
     */
    public boolean existeixSolucio() {
        System.out.println("**BUSCAN SOLUCIO MILLOR**");
        backtracking();
        System.out.println();
        if (_trobat) _optim.finalitzar();
        return _trobat;
    }
}
