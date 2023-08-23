package mycasestudy;

import pli.SymHeap;

public class MyMain {

    public static void main(String[] args) {
        TreeMap structure = new TreeMap();
        structure = (TreeMap) SymHeap.makeSymbolicRefThis("treemap_0", structure);
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        structure.containsKey(key); // Call to method under analysis

        SymHeap.pathFinished();
    }
}