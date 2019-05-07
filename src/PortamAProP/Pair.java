/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PortamAProP;

/**
 *
 * @author wodash
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
