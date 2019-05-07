package PortamAProP;

/**
 * @class SolucionadorRuta
 * @brief Algoritme recursiu que s'encarrega de trobar la millor solucio.
 * @author Xavier Avivar & Buenaventura Martinez
 * @TODO Adaptar l'algoritme perque retorni la millor solucio, comencem per fer
 * que retorni una unica solucio
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
     * @post Busca una unica solucio al problema, plega quan la trobat
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
    /*private void backtracking() {
        //backtracking una solucio
        CandidatRuta iCan = _actual.iniCan();
        while (!iCan.esFi() && !_trobat) {
            if (_actual.acceptable(iCan)) {
                _actual.anotar(iCan);
                if (!_actual.completa()) {
                    backtracking();
                    //std::cout<<"sup"<<'\n';
                    if (!_trobat) {
                        _actual.desanotar(iCan);
                    }
                } else {
                    _trobat = true;
                }
            }
            iCan.seguent();
        }
    }*/

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
     * @brief Ens dona la solucio trobada
     * @return SolucioRuta, objecte que conte la solucio potencial al algoritme
     */
    public SolucioRuta obtSolucio() {
        return _optim;
    }

    /**
     * @brief Ens diu si existeix solucio
     * @param sol SolucioRuta, objecte que conte la solucio potencial al
     * algoritme
     * @return
     */
    public boolean existeixSolucio(SolucioRuta sol) {
        _actual = sol;
        System.out.println("**BUSCAN SOLUCIO MILLOR**");
        backtracking();
        System.out.println();
        return _trobat;
    }
}
