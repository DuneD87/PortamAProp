package PortamAProP;

/**
 * @author Buenaventura
 * @description Objecte que serveix per assomir les peticions
 */
public class Vehicle {
    //Atributs
    private int _identificador; //Indetificador unic per el vehicle
    private int _capacitatTotal; //Quantitat total de persones que pot portar
    private double _autonomiaTotal; //Temps total que pot funcionar el vehicle sense haber de cargar
    private double _autonomiaRestant; //Temps que li falta per haber de anar a cargar
    private int _numPassatgers; //Numero de passatges actuals del vehicle
    private double _carga; //Index de efectivitat  carga del cotxe 0<x<1 (no pot ser 0 perque no cargaria)
    private int _idNodePrincipi; // Identificador del node en que el cotxe esta inicialment
    //Constructors

    public Vehicle() {
        
    }
    public Vehicle(int Id, int CapTotal, double AutoTot, double carga, int node){
        _identificador=Id;
        _capacitatTotal=CapTotal;
        _autonomiaTotal=AutoTot;
        _numPassatgers=0;
        _autonomiaRestant=AutoTot;
        _carga=carga;
        _idNodePrincipi=node;
    }
    //Metodes publics
    
    //Pre: 0<Abs(variacio)<= _CapacitatTotal
    //Post: Ens modifica el valor del atribut _CapacitatTotal segons variacio
    public void ModificarPassatgers(int variacio){
           _numPassatgers+=variacio;
    }
    
    //Pre: 0<temps
    //Post: Ens resta temps al atribut _autonomiaRestant i el deixa a 0 si aquest ultim es negatiu
    public void descarga(int temps){
        _autonomiaRestant-=temps; //Preguntar si possar index de descarga
        if(_autonomiaRestant<0)
            _autonomiaRestant=0;
    }
    
    //Pre: 0<temps
    //Post: S'ha cargat el vehicle en funcio del temps
    public void cargar(int temps){
        _autonomiaRestant=_autonomiaRestant+(temps*_carga);
        if(_autonomiaRestant>_autonomiaTotal)
            _autonomiaRestant=_autonomiaTotal;
    }
    
    public String toString(){
        String sortida = "Identificador: " + _identificador + "\n"
                + "Autonomia Total: " + _autonomiaTotal + "\n"
                + "Autonomia Restant: " + _autonomiaRestant + "\n"
                + "CapacitatTotal " + _capacitatTotal + "\n"
                + "Nombre de passatgers: " + _numPassatgers + "\n"
                + "=============================================";
         
        return sortida;
    }
    
    
    
}

