package PortamAProP;


import java.util.Vector;
import java.io.*;

public class LlegirFitxersVehicle {

    private Vector<Vehicle> _vecVehicles = new Vector<Vehicle>(50);//vector global de vehicles

    public void init() {
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fitxer = new File("//home/wodash/Escritorio/PC/2nQuatri/Projecte de programacio/ProjecteGran/FitxersConfiguracio/Vehicles.txt");
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);

            String linia;
            while ((linia = br.readLine()) != null) {
                Crearvehicle(linia);
            }
            for (Vehicle v : _vecVehicles) {
                System.out.println(v);
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

    //Pre: linia = string valid per configuracio de vehicle
    //Post: Retorna el vehicle creat a partir de linia i l'afageix al vector de vehicles
    public void Crearvehicle(String linia) {
        String[] parts = linia.split(" ");
        Vehicle v = new Vehicle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        _vecVehicles.add(v);
    }

}
