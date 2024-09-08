package expr.treemap;

import gov.nasa.jpf.symbc.Debug;

public class Example {

    public static void main(String[] args) {
        TreeMap tree = new TreeMap();
        tree = (TreeMap) Debug.makeSymbolicRef("tree", tree);

        if (tree != null) {
        	if (tree.repOKComplete()) {
        		System.out.println("true");
        	} else {
        		//System.out.println("false");
        	}
        }

    }
}
