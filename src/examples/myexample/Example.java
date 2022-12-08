package myexample;

import heapsolving.treemap.TreeMap;
import lissa.SymHeap;

public class Example {

    public static void main(String[] args) {
        TreeMap tree = new TreeMap();
        for (int i = 1; i <= 7; i++) {
            tree.put(SymHeap.makeSymbolicInteger("N" + i), new Object());
        }
    }
}
