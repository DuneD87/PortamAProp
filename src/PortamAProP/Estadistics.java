package PortamAProP;
/**
 * @class Estadistics
 * @brief Classe encarregada de generar els estadistics globals de totes les rutes solucionades pel backtraking
 *        
 *        La classe Estadistic es l'encarregada de generar els estadistics correponents 
 *        de totes les rutes en conjunt, a la vegada tambe es l'encarregada de generar els
 *        els gàfics.
 * 
 * @author Xavier Avivar & Buenaventura Martinez
 */
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class Estadistics {
    
    private ArrayList<Ruta> _rutes; //!<@brief Llista de totes les rutes solucionades pel backtracking
    private double _mitjanaTempsRutaVehicle;//!<@brief Mitjana de temps que el vehicle esta circulant per la carratera
    private double _mitjanaTempsCarregaVehicle;//!<@brief Mitjana de temps que el vehicle esta aturat per carregar
    private double _mitjanaPassatgersVehicle;//!<@brief Mitjana de clients per peticio
    private int _distanciaNodes;//!<@brief Mitjana de la distancia (en minuts) que hi ha entre els nodes de la ruta
    private int _mitjanaTempsEsperaClient; //!<@brief Mitjana de temps que els clients esperen a ser recollits pel vehicle
    private int _mitjanaTempsRecorregutClient;//!<@brief Mitjana de temps que el client esta al vehicle, desde que el recull fins que el deixa
    
    /*FINESTRES*/
    private JFreeChart chartMitjanaTempsMarxaVehicle;
    private JFreeChart chartMitjanaTempsCarregaVehicle;
    private JFreeChart chartMitjanaDistanciaNodes;
    private JFreeChart chartMitjanaPersonesVehicle;
    private JFreeChart chartMitjanaEsperaClient;
    private JFreeChart chartMitjanaRecorregutClient;
    
    /**
     * @brief Constructor de la classe
     * @pre rutes.size>0
     * @post Assigna rutes com la llista de rutes i inisialitza tots els atributs a 0
     * @param rutes llista amb totes les rutes 
     */
    public Estadistics(ArrayList<Ruta> rutes) {
        _rutes = rutes;
        _mitjanaTempsRutaVehicle=0;
        _mitjanaTempsCarregaVehicle=0;
        _mitjanaPassatgersVehicle=0;
        _distanciaNodes=0;
        _mitjanaTempsEsperaClient=0;
        _mitjanaTempsRecorregutClient=0;

    }

    /**
     * @brief Metode que retorna la mitjana, de totes les rutes, del temps que
     *      el vehicle esta circulant amb clients. 
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades el temps en marxa del vehicle 
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de temps per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Mitjana de temps que el vehicle esta ciculant amb clients
     * 
     */
    public double mitjanaTempsMarxaVehicle() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int finalitzades = 0;
        for (int i = 0; i < _rutes.size(); i++) {
            if (_rutes.get(i).finalitzada()) {
                double distancia = _rutes.get(i).tempsEnMarxa();
                dataset.setValue(distancia, "Rutes", "" + i);
                _mitjanaTempsRutaVehicle += distancia;
                finalitzades++;

            }
        }
        _mitjanaTempsRutaVehicle/=finalitzades;
        chartMitjanaTempsMarxaVehicle = ChartFactory.createBarChart("Temps Marxa Vehicle", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        //generarFinestra(chartMitjanaTempsMarxaVehicle);
        return _mitjanaTempsRutaVehicle;
    }
    
    
        /**
     * @brief Metode que retorna la mitjana, de totes les rutes, del temps que
     *      el vehicle esta carregant.
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades el temps en carrega del vehicle 
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de temps per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Mitjana de temps que el vehicle esta carregant
     * 
     */
    public double mitjanaTempsCarregaVehicle() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int finalitzades = 0;
        for (int i = 0; i < _rutes.size(); i++) {
            if (_rutes.get(i).finalitzada()) {
                double distancia = _rutes.get(i).tempsDepot();
                dataset.setValue(distancia, "Rutes", "" + i);
                _mitjanaTempsCarregaVehicle += distancia;
                finalitzades++;

            }
        }
        _mitjanaTempsCarregaVehicle/=finalitzades;
        chartMitjanaTempsCarregaVehicle = ChartFactory.createBarChart("Temps Carrega Vehicle", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        //generarFinestra(chartMitjanaTempsCarregaVehicle);
        return _mitjanaTempsCarregaVehicle;
    }
    
        /**
     * @brief Metode que retorna la mitjana, de totes les rutes, del nombre de clients
     *        per solicitud
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades el nombre mig  de clients per peticio 
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de clients per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Mitjana de clients per peticio.
     * 
     */
    public double mitjanaPassatgers() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int finalitzades = 0;
        for (int i = 0; i < _rutes.size(); i++) {
            if (_rutes.get(i).finalitzada()) {
                double pass = _rutes.get(i).gmitjanaPassatgers();
                dataset.setValue(pass, "Rutes", "" + i);
                _mitjanaPassatgersVehicle += pass;
                finalitzades++;
            }
        }
        _mitjanaPassatgersVehicle /= finalitzades;
        chartMitjanaPersonesVehicle= ChartFactory.createBarChart("Nombre Passatgers", "", "Num. Passatgers", dataset, PlotOrientation.VERTICAL, true, true, false);
        //generarFinestra(chartMitjanaPersonesVehicle);
        return _mitjanaPassatgersVehicle;

    }


        /**
     * @brief Metode que retorna la mitjana, de totes les rutes, de la distancia entre els nodes de les rutes
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades, la mitjana de la distancia entre nodes de la ruta
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de la distancia per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Distancia promig (en minuts) entre els nodes de les rutes
     * 
     */
    
    public double mitjanaDistanciaNodes(){
        DefaultCategoryDataset dataset= new DefaultCategoryDataset();
        int finalitzades = 0;
        for(int i=0;i<_rutes.size();i++){
            if(_rutes.get(i).finalitzada()){
                double dist = _rutes.get(i).gmitjanaDistanciaNodes();
                dataset.setValue(dist,"Rutes",""+i);
                _distanciaNodes+=dist;
                finalitzades++;
            
            }
        }
        _distanciaNodes/=finalitzades;
        chartMitjanaDistanciaNodes = ChartFactory.createBarChart("Distancia entre Nodes", "", "Minuts", dataset,PlotOrientation.VERTICAL,true,true,false);
        //generarFinestra(chartMitjanaDistanciaNodes);
        return _distanciaNodes;
    }
    
        /**
     * @brief Metode que retorna la mitjana, de totes les rutes, del temps 
     *      que el client espera a ser ates
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades el temps mig d'espera del client  
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de temps d'espera per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Mitjana de temps que el client espera a ser ates 
     * 
     */
        
    public double mitjanaTempsEsperaClient() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int finalitzades = 0;
        for (int i = 0; i < _rutes.size(); i++) {
            if (_rutes.get(i).finalitzada()) {
                double temps = _rutes.get(i).gmitjanaTempsEsperaClient();
                dataset.setValue(temps, "Rutes", "" + i);
                _mitjanaTempsEsperaClient += temps;
                finalitzades++;

            }
        }
        _mitjanaTempsEsperaClient/=finalitzades;
        chartMitjanaEsperaClient = ChartFactory.createBarChart("Temps Espera Client", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        //generarFinestra(chartMitjanaEsperaClient);
        return _mitjanaTempsEsperaClient;
    }
    
            /**
     * @brief Metode que retorna la mitjana, de totes les rutes, del temps 
     *      del client desde que puja al vehicle fins que arriba al seu desti
     * 
     *      Per aconseguir la mitjana, busca per totes les rutes finalitzades el temps mig de circulacio del client  
     *      i ho va sumant, a la vegada va portant el compte del numero de rutes 
     *      finalitzades, per acabar, divideix la suma total de temps de circulacio per el nombre 
     *      de rutes finalitzades, finalment mostra el grafic per pantalla
     * 
     * @post Retorna la Mitjana de temps que el client esta circulant fins arribar al seu desti
     * 
     */
        
     public double mitjanaTempsMarxaClient() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int finalitzades = 0;
        for (int i = 0; i < _rutes.size(); i++) {
            if (_rutes.get(i).finalitzada()) {
                double temps = _rutes.get(i).gmitjanaTempsMarxaClient();
                dataset.setValue(temps, "Rutes", "" + i);
                _mitjanaTempsRecorregutClient += temps;
                finalitzades++;

            }
        }
        _mitjanaTempsRecorregutClient/=finalitzades;
        chartMitjanaRecorregutClient = ChartFactory.createBarChart("Temps Marxa Client", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        //generarFinestra(chartMitjanaRecorregutClient);
        return _mitjanaTempsRecorregutClient;
    }
            /**
     * @brief Metode que mostra en un grafic per pantalla les dades emmagatzemades a chart
     * 
     * @post Mostra un grafic per pantalla amb les dades de chart
     * @param chart Contenidor de dades del qual es fara el grafic
     * 
     */
        
    public void generarFinestra(JFreeChart chart){
         ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void mostrarEstadistic(int format){
        switch(format){
            case 1: 
                generarFinestra(chartMitjanaTempsMarxaVehicle);
                break;
            case 2:
                generarFinestra(chartMitjanaTempsCarregaVehicle);
                break;
            case 3:
                generarFinestra(chartMitjanaPersonesVehicle);
                break;
            case 4:
                generarFinestra(chartMitjanaDistanciaNodes);
                break;
            case 5:
                generarFinestra(chartMitjanaRecorregutClient);
                break;
            case 6:
                generarFinestra(chartMitjanaEsperaClient);
        }
    }
    
   
    
    
}
