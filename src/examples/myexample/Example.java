package myexample;

import gov.nasa.jpf.symbc.Debug;
import heapsolving.treemap.TreeMap;

public class Example {

    public static void main(String[] args) {
        TreeMap tree = new TreeMap();
        tree = (TreeMap) Debug.makeSymbolicRef("tree", tree);

//        if (tree != null && tree.repOKStructure()) {
//            tree.put(Debug.makeSymbolicInteger("INPUTKEY"), new Object());
//        }

    }
}
