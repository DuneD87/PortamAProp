package PortamAProP;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class GeneradorSolicituds {

  
    private int _maxNodes = 20;
    private String _sol;
    private static int MAX_SOL = 20;
    public GeneradorSolicituds() {

        int numero;
        //Scanner teclado = new Scanner(System.in);
        //System.out.println("Quantes solicituds vols fer?");
        //numero = Integer.parseInt(teclado.nextLine());
        numero = MAX_SOL;
        _sol = Crear_solicituds(numero);
        System.out.print(_sol);
        /*System.out.println("Vols fer un fitxer amb aqustes solicituds? [S/N]");
        String eleccio = teclado.nextLine();
        if (eleccio.equals("S")) {
            System.out.println("Nom del fitxer:");
            String nomf = teclado.nextLine();
            CrearFitxer(nomf, _sol);

        }*/

    }

    //Pre: 0<numero
    //Post: Retorna un string amb una llista de solicituds creades
    private String Crear_solicituds(int numero) {
        Random random = new Random();
        String llistat = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < numero; i++) {
            llistat += i + " ";
            int random1 = random.nextInt(_maxNodes);
            int random2;
            do {
                random2 = random.nextInt(_maxNodes);
            } while (random1 == random2);
            llistat += random1 + " ";//id node sortida
            llistat += random2 + " ";//id node entrada
            llistat += random.nextInt(24) + " ";//hora emisio
            llistat += random.nextInt(60) + " ";//minut emisio
            llistat += random.nextInt(60) + " ";//segon emisio
            llistat += random.nextInt(8) + 1;//numero de passatgers, entre 1 i 9
            llistat += "\n";

        }
        return llistat;
    }

    //Pre: -
    //Post: Crea un fitxer de nom nomf i amb el contingut de sol
    private void CrearFitxer(String nomf, String sol) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(nomf);
            pw = new PrintWriter(fichero);

            pw.println(sol);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    /**
     * @brief Ens dona les solicituds generades aleatoriament
     * @pre ---
     * @return Cadena de caracters amb les solicituds
     */
    @Override
    public String toString() {
        return _sol;
    }
}
