package PortamAProP;

import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 * @brief Classe encarregada de generar els estadistics globals de totes les rutes solucionades pel backtraking
 *        
 *        La classe Estadistic es l'encarregada de generar els estadistics correponents 
 *        de totes les rutes en conjunt, a la vegada tambe es l'encarregada de generar els
 *        els gàfics.
 * @author Xavier Avivar & Buenaventura Martinez
 */

public class Estadistics {
    
    private ArrayList<Ruta> _rutes; //@brief Llista de totes les rutes solucionades pel backtracking
    private double _mitjanaTempsRutaVehicle;//@brief Mitjana de temps que el vehicle esta circulant per la carratera
    private double _mitjanaTempsCarregaVehicle;//@brief Mitjana de temps que el vehicle esta aturat per carregar
    private double _mitjanaPassatgersVehicle;//@brief Mitjana de clients per peticio
    private int _distanciaNodes;//@brief Mitjana de la distancia (en minuts) que hi ha entre els nodes de la ruta
    private int _mitjanaTempsEsperaClient; //@brief Mitjana de temps que els clients esperen a ser recollits pel vehicle
    private int _mitjanaTempsRecorregutClient;//@brief Mitjana de temps que el client esta al vehicle, desde que el recull fins que el deixa
    
    
    /**
     * @brief Constructor de la classe
     * @pre rutes.size>0
     * @post Assigna rutes com la llista de rutes i inisialitza tots els atributs a 0
     * @param rutes 
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
     *      de rutes finalitzades
     * 
     * @post Retorna la Mitjana de temps que el vehicle esta ciculant amb clients
     * @return 
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
        JFreeChart chart = ChartFactory.createBarChart("Temps Marxa Vehicle", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return _mitjanaTempsRutaVehicle;
    }
    
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
        JFreeChart chart = ChartFactory.createBarChart("Temps Carrega Vehicle", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return _mitjanaTempsCarregaVehicle;
    }
    
    
  
    public double mitjanaPassatgers(){
        DefaultCategoryDataset dataset=new DefaultCategoryDataset();
          int finalitzades = 0;
        for(int i=0;i<_rutes.size();i++){
            if(_rutes.get(i).finalitzada()){
                 double pass = _rutes.get(i).gmitjanaPassatgers();
                 dataset.setValue(pass,"Rutes",""+i);
                 _mitjanaPassatgersVehicle=+pass;
                 finalitzades++;
                        
            
            }
        }
        _mitjanaPassatgersVehicle/=finalitzades;
        JFreeChart chart = ChartFactory.createBarChart("Nombre Passatgers", "", "Num. Passatgers", dataset,PlotOrientation.VERTICAL,true,true,false);
        generarFinestra(chart);
        return _mitjanaPassatgersVehicle;
    
    }
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
        JFreeChart chart = ChartFactory.createBarChart("Distancia entre Nodes", "", "Minuts", dataset,PlotOrientation.VERTICAL,true,true,false);
        ChartPanel panel = new ChartPanel(chart);
        generarFinestra(chart);
        return _distanciaNodes;
    }
    
        
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
        JFreeChart chart = ChartFactory.createBarChart("Temps Espera Client", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(chart);
        generarFinestra(chart);
        return _mitjanaTempsCarregaVehicle;
    }
    
    
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
        JFreeChart chart = ChartFactory.createBarChart("Temps Marxa Client", "", "Minuts", dataset, PlotOrientation.VERTICAL, true, true, false);
        generarFinestra(chart);
        return _mitjanaTempsCarregaVehicle;
    }
    
    public void generarFinestra(JFreeChart chart){
         ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
}
