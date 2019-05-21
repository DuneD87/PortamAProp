package PortamAProP;



public class PortamAProp {
    /**
     * @param args the command line argumentsds
     * @arg -tf n   Temps de finestra acompanyat d'un nombre (DEFAULT : 150min)
     * @arg -me n   Temps maxim d'espera de les peticions (DEFAULT : 30min)
     * @arg -ml n   Temps minim legal per atendre una peticio (DEFAULT : 15min)
     * @arg -rs n,k Creara maxim n peticions aleatories per el paquet actual, amb k persones maxim (DEFAULT: ON : 60 peticions : 2 persones maxim per peticio) 
     * @arg -fs f   Treballarem sobre un paquet de peticions que venen de fitxer (DEFAULT: OFF)
     * @arg -rn n,k Creara n nodes aleatoris per el nostre graf amb k pes maxim (DEFAULT: ON : 60 nodes : 20 pes maxim)
     * @arg -fn f   Creara un graf llegit desde fitxer (DEFAULT: OFF)
     * @arg -mg n   Distancia maxima que el l'algoritme per assignar peticions posara com a restriccio (DEFAULT: 50min)
     * @arg -mc n   Ens diu el minim de carrega que el vehicle ha de tenir en % abans de considerar un punt de carrega com acceptable (DEFAULT: 0.8)
     * @arg -o  f   Ens diu el nom del fitxer de sortida (DEFAULT: output.txt)
     * @arg -help   Ens dona informacio sobre l'utilitzacio de la aplicacio
     */
    
    public static void main(String[] args) {
        
        /**VARIABLES DE CONTROL*/
        int tamanyFinestra = 150;
        int maximEspera = 30;
        int minimLegal = 15;
        int nPeticions = 60;
        int maxPersones = 2;
        String nFitxerSol = "peticions.txt";
        int nNodes = 60;
        int pesMaxim = 20;
        String nFitxerGraf = "Graf.txt";
        int maxGreedy = 50;
        boolean randomSol = false;
        boolean randomNode = false;
        double minCarga = 0.8;
        String nFitxerSortida = "output.txt";
        /**TRACTEM ELS ARGUMENTS I SI CAL, ACTUALITZEM LES VARIABLES*/
        
        for (int i = 0 ; i < args.length; i++) {
            String s = args[i];
            if (s.contains("-tf")) {
                tamanyFinestra = Integer.parseInt(args[i + 1]);
            } else if (s.contains("-me")) {
                maximEspera = Integer.parseInt(args[i + 1]);
            } else if (s.contains("-ml")) {
                minimLegal = Integer.parseInt(args[i + 1]);
            } else if (s.contains("-rs")) {
                nPeticions = Integer.parseInt(args[i + 1]);
                maxPersones = Integer.parseInt(args[i + 2]);
            } else if (s.contains("-fs")) {
                randomSol = false;
                nFitxerSol = args[i + 1];
            } else if (s.contains("-rn")) {
                nNodes = Integer.parseInt(args[i + 1]);
                pesMaxim = Integer.parseInt(args[i + 2]);
            } else if (s.contains("-fn")) {
                randomNode = false;
                nFitxerGraf = args[i + 1];
            } else if (s.contains("-mg")) {
                maxGreedy = Integer.parseInt(args[i + 1]);
            } else if (s.contains("-mc")) {
                minCarga = Double.parseDouble(args[i + 1]);
            } else if (s.contains("-o")) {
                nFitxerSortida = args[i + 1];
            }
        }
        
        /**INICIALITZEM EL CONTROLADOR*/
        Controlador c = new Controlador(tamanyFinestra,maximEspera,minimLegal,nPeticions,maxPersones,nFitxerSol,nNodes,pesMaxim,nFitxerGraf,maxGreedy,randomSol, randomNode,minCarga,nFitxerSortida);
        c.init();
    }

}


