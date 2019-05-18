package PortamAProP;

import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class Estadistics {
    
    private ArrayList<Ruta> _rutes;
    private double _mitjanaTempsRutaVehicle;
    private double _mitjanaTempsCarregaVehicle;
    private double _mitjanaPassatgersVehicle;
    private int _distanciaNodes;
    private int _mitjanaTempsEsperaClient;
    private int _mitjanaTempsRecorregutClient;
    
    public Estadistics(ArrayList<Ruta> rutes) {
        _rutes = rutes;
        _mitjanaTempsRutaVehicle=0;
        _mitjanaTempsCarregaVehicle=0;
        _mitjanaPassatgersVehicle=0;
        _distanciaNodes=0;
        _mitjanaTempsEsperaClient=0;
        _mitjanaTempsRecorregutClient=0;

    }

    
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
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return _mitjanaTempsCarregaVehicle;
    }
    
    
    
    
}
