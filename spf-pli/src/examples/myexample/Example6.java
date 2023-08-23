package myexample;

import gov.nasa.jpf.symbc.Debug;

public class Example6 {

    public static void main(String[] args) {

        int x = Debug.makeSymbolicInteger("x");
        int y = Debug.makeSymbolicInteger("y");

        if (y > 1 || y < 0) {
            System.out.println("\nthen y");
            // return;
        }
        if (x > 14 || x < 0) {
            System.out.println("\nthen x");
            // System.out.println("\ninside: " + Debug.getSolvedPC());
        }

        System.out.println("\n" + Debug.getSolvedPC());
//        int j = 1;
//        System.out.println("\n j = " + j);

    }
}
