package myexample;

import gov.nasa.jpf.symbc.Debug;

public class Example2 {
    public static void main(String[] args) {

        int X = Debug.makeSymbolicInteger("X");
        if (X == 0) {
            if (X >= 0) {
                // System.out.println("\n" + Debug.getSolvedPC());
            } else {
                // System.out.println("\n" + Debug.getSolvedPC());
            }
        }
        System.out.println("\n" + Debug.getSolvedPC());
        System.out.println("\nExecuted!!!");
    }
}
