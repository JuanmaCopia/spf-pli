package myexample;

import gov.nasa.jpf.symbc.Debug;
import heapsolving.treemap.TreeMap;

public class Example2 {

    public static void main(String[] args) {
        TreeMap tree = new TreeMap();
        tree = (TreeMap) Debug.makeSymbolicRef("tree", tree);
        System.out.println("\nfinished!");
    }
}
