

package PortamAProP;

/**
 * @class Pair
 * @brief Guarda informacio sobre 2 objectes
 * @author Xavier Avivar & Buenaventura Martinez
 */
public class Pair <S,T> {
    private S first;
    private T second;
    
    public Pair (S f,T s){
        first=f;
        second=s;
    }
    
    public  S getKey(){
        return first;
    }
    
    public T getValue(){
        return second;
    }
}
