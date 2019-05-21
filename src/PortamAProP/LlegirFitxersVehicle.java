package PortamAProP;


import java.util.Vector;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LlegirFitxersVehicle {

    private List<Vehicle> _vecVehicles;
    private String FITXER_VEHICLE;
    /**
     * @brief Inicialitza la lectura de vehicles
     * @pre ---
     * @post S'ha inicialitzat la lectura de vehicles
     */
    public LlegirFitxersVehicle(String nFitxer) {
        FITXER_VEHICLE = nFitxer;
        _vecVehicles = new ArrayList<>();
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            Scanner sc = new Scanner(System.in);
            //System.out.println("Nom del fitxer de vehicles:");
            //String nom = sc.nextLine();
            String nom = FITXER_VEHICLE;
            fitxer = new File(nom);
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);

            String linia;
            while ((linia = br.readLine()) != null) {
                if(linia.charAt(0)!='#')
                    Crearvehicle(linia);
                 
            }
           
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
    }

    /**
     * @brief Crea un vehicle
     * @pre linia = string valid per configuracio de vehicle
     * @post Ens crea un vehicle a partir de linia i l'afegeix a l'estructura
     */
    private void Crearvehicle(String linia) {
        String[] parts = linia.split(" ");
        Vehicle v = new Vehicle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
        _vecVehicles.add(v);
    }
    
    /**
     * @brief Ens dona l'estructura que conte els vehicles
     * @pre S'ha cridat a init previament
     * @post Ens dona l'estructura que conte els vehicles
     */
    public List<Vehicle> obtVehicles() {
        return _vecVehicles;
    }
}

