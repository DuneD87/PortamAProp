package proprotest;


import java.util.Vector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Time;

public class LlegirFitxerSolicitud {

   private Vector<Solicitud> _vecSol = new Vector<Solicitud>(50);

    public void init() {
        File fitxer = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fitxer = new File("//home/wodash/Escritorio/PC/2nQuatri/Projecte de programacio/ProjecteGran/FitxersConfiguracio/Solicituds.txt");
            fr = new FileReader(fitxer);
            br = new BufferedReader(fr);

            String linia;
            while ((linia = br.readLine()) != null) {
                CrearSolicitud(linia);
            }
            for (Solicitud s : _vecSol) {
                System.out.println(s);
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
    //Post: s valid per a configuracio de solicitud
    //Post: Crea una nova solicitud i la afageix al vector de solicituds

    public void CrearSolicitud(String s) {
        String[] parts = s.split(" ");
        Time emisio = new Time(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Solicitud sol = new Solicitud(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), emisio, Integer.parseInt(parts[8]));
        _vecSol.add(sol);

    }
}
