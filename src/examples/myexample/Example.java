package myexample;

import gov.nasa.jpf.symbc.Debug;

public class Example {

    public static void main(String[] args) {
//        TreeMap tree = new TreeMap();
//        tree = (TreeMap) Debug.makeSymbolicRef("tree", tree);

        Integer X = null;
        X = Debug.makeSymbolicInteger("X");

        if (X == null)
            System.out.println("null!!");
        else
            System.out.println("not null");

//        if (tree != null && tree.repOKStructure()) {
//            tree.put(Debug.makeSymbolicInteger("INPUTKEY"), new Object());
//        }

    }
}
