package myexample;

import lissa.SymHeap;

public class Example5 {

    public static void main(String[] args) {

        Node root = new Node();
        root = (Node) SymHeap.makeSymbolicRefThis("nodeRoot", root);

        String X = SymHeap.makeSymbolicString("X");
        if (root != null) {
            if (root.key != null && X != null) {
                comp(root.key, X);
            }
        }

    }

    public static void comp(String a, String b) {
        if (a != null && b != null) {
            int comp = a.compareTo(b);
            if (comp == -1) {
                System.out.println("root.key <  X");
            } else if (comp == 0) {
                System.out.println("root.key == X");
            } else if (comp == 1) {
                System.out.println("root.key >  X");
            } else {
                assert false; // ERROR!
            }
        }
    }

    public static void c(String a, String b) {
        if (a != null && b != null)
            a.concat(b);
    }
}
