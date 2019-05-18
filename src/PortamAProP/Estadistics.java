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
    
    public Estadistics(ArrayList<Ruta> rutes){
        _rutes=rutes;
    }
    
    public void mostrarMitjanaPassatgers(){
        DefaultCategoryDataset dataset=new DefaultCategoryDataset();
        for(int i=0;i<_rutes.size();i++){
            dataset.setValue(_rutes.get(i).mitjanaPassatgers(),"Rutes",""+i);
        }
        JFreeChart chart = ChartFactory.createBarChart("Mitjana Passatgers", "", "", dataset,PlotOrientation.VERTICAL,true,true,false);
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    }
    public void mostrarMitjanaDistanciaNodes(){
        DefaultCategoryDataset dataset= new DefaultCategoryDataset();
        for(int i=0;i<_rutes.size();i++){
            dataset.setValue(_rutes.get(i).tempsEnMarxa(),"Rutes",""+i);
        }
        JFreeChart chart = ChartFactory.createBarChart("Mitjana Temps vehicle a Carratera", "", "", dataset,PlotOrientation.VERTICAL,true,true,false);
        ChartPanel panel = new ChartPanel(chart);
        JFrame ventana = new JFrame("Grafic");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    
}