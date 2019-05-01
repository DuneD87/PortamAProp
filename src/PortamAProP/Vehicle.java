package PortamAProP;

/**
 * @brief Classe encarregada de guardar dades dels vehicles
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class Vehicle {
    //Atributs
    private int _identificador; //@brief Indetificador unic per el vehicle
    private int _capacitatTotal; //@brief Quantitat total de persones que pot portar
    private double _autonomiaTotal; //@brief Temps total que pot funcionar el vehicle sense haber de cargar
    private double _autonomiaRestant; //@brief Temps que li falta per haber de anar a cargar
    private int _numPassatgers; //@brief Numero de passatges actuals del vehicle
    private double _carga; //@brief Index de efectivitat  carga del cotxe 0<x<1 (no pot ser 0 perque no cargaria)
    private int _idNodePrincipi; //@brief Identificador del node en que el cotxe esta inicialment
    private int _idNodeActual; //@brief Identificador del node en el que es troba el cotxe
   
    /**
     * @brief Constructor amb parametres
     * @pre ---
     * @post S'ha construit un nou vehicle
     */
    public Vehicle(int Id, int CapTotal, double AutoTot, double carga, int node){
        _identificador=Id;
        _capacitatTotal=CapTotal;
        _autonomiaTotal=AutoTot;
        _numPassatgers=0;
        _autonomiaRestant=AutoTot;
        _carga=carga;
        _idNodePrincipi=node;
        _idNodeActual = _idNodePrincipi;
    }
    
    
    /**
     * @brief Canvia el numero de passatges
     * @pre 0<Abs(variacio)<= _CapacitatTotal
     * @post Ens modifica el valor del atribut _CapacitatTotal segons variacio
     */
    public void ModificarPassatgers(int variacio){
           _numPassatgers+=variacio;
    }

    /**
     * @brief Fa que el vehicle perdi carrega progressivament
     * @pre 0 < temps
     * @post Ens resta temps al atribut _autonomiaRestant i el deixa a 0 si aquest ultim es negatiu
     */
    public void descarga(double temps){
        _autonomiaRestant-=temps; //Preguntar si possar index de descarga
        if(_autonomiaRestant<0)
            _autonomiaRestant=0;
    }
    
    /**
     * @brief Carrega el vehicle
     * @pre 0 < temps
     * @post S'ha carregat el vehicle en funcio del temps
     */
    public void cargar(double temps){
        _autonomiaRestant=_autonomiaRestant+(temps*_carga);
        if(_autonomiaRestant>_autonomiaTotal)
            _autonomiaRestant=_autonomiaTotal;
    }
    
    /**
     * @brief Ens dona informacio sobre el vehicle
     * @pre ---
     * @post Torna una cadena de caracters amb informacio sobre el vehicle
     */
    @Override
    public String toString(){
        String sortida = "Identificador: " + _identificador + "\n"
                + "Autonomia Total: " + _autonomiaTotal + "\n"
                + "Autonomia Restant: " + _autonomiaRestant + "\n"
                + "CapacitatTotal " + _capacitatTotal + "\n"
                + "Nombre de passatgers: " + _numPassatgers + "\n"
                + "=============================================";
         
        return sortida;
    }
    
    /**
     * @brief Nombre de passatgers
     * @pre ---
     * @post Ens diu el nombre de passatges que te actualment el vehicle
     */
    public int nPassatgers() {
        return _numPassatgers;
    }
    /**
     * @brief Total de passatgers
     * @pre ---
     * @post Ens diu el total de passatgers que pot assolir el vehicle
     */
    public int nPassTotal() {
        return _capacitatTotal;
    }
    
    /**
     * @brief Node inicial
     * @pre ---
     * @post Ens diu el node inicial del vehicle
     */
    public int nodeInicial() {
        return _idNodePrincipi;
    }
    
    /**
     * @brief Actualitza la posicio del vehicle
     * @pre 0 <= nodeId >= maxNodesGraf
     * @post S'ha actualitzat l'identificador del node on es troba el vehicle
     */
    public void setPosicio(int nodeId) {
        _idNodeActual = nodeId; 
    }
    
    /**
     * @brief Ens dona la posicio actual del vehicle
     * @pre ---
     * @post Ens dona l'identificador del node on actualment es troba el vehidle
     */
    public int getPosicio() {
        return _idNodeActual;
    }
    
    /**
     * @brief Autonomia restant del vehicle
     * @pre ---
     * @post Ens diu la quantitat de carrega que li queda al vehicle
     */
    public double carregaRestant() {
        return _autonomiaRestant;
    }
    
    /**
     * @brief Autonomia total del vehicle
     * @pre ---
     * @post Ens diu la quantitat de carrega total que pot enmagatzemar el vehicle
     */
    public double carregaTotal() {
        return _autonomiaTotal;
    }
    
    /**
     * @brief Assigna l'autonimia total del vehicle a l'autonomia restant
     * @pre ---
     * @post Ens diu la quantitat de carrega total que pot enmagatzemar el vehicle
     */
    public void restaurarCarrega(){
        _autonomiaRestant=_autonomiaTotal;
    }
}

