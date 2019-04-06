package proprotest;


import java.util.Date;
import java.util.Random;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;
import org.graphstream.*;
import org.graphstream.algorithm.generator.ChainGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;
import org.graphstream.algorithm.generator.FlowerSnarkGenerator;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.lcf.Balaban10CageGraphGenerator;
import org.graphstream.algorithm.generator.lcf.BiggsSmithGraphGenerator;
import org.graphstream.algorithm.generator.lcf.CubicalGraphGenerator;
import org.graphstream.algorithm.generator.lcf.DyckGraphGenerator;
import org.graphstream.algorithm.randomWalk.RandomWalk;
import sun.nio.cs.Surrogate;

public class PortamAProp {

    /**
     * @param args the command line argumentsdsfds
     */
    public static void main(String[] args) {
        Controlador c = new Controlador();
        c.init();
    }

}
