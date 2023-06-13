package myexample;

import pli.SymHeap;

public class Example3 {

    public static void main(String[] args) {

//        Node root = new Node();
//        root = (Node) SymHeap.makeSymbolicRefThis("nodeRoot", root);

        String X = SymHeap.makeSymbolicString("X");
        String Y = SymHeap.makeSymbolicString("Y");
        int comp = X.compareTo(Y);
        if (comp == -1) {
            System.out.println("Y > X");
        } else if (comp == 0) {
            System.out.println("Y == X");
        } else if (comp == 1) {
            System.out.println("Y < X");
        } else {
            assert false; // ERROR!
        }

//        System.out.println("\n" + Debug.getSolvedPC());
//        System.out.println("\nExecuted!!!");
    }
}
