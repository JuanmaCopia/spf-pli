package expr.treemap;

import expr.Peer;

public class Example {

    public static void main(String[] args) {
        TreeMap tree = new TreeMap();
        tree = (TreeMap) Peer.makeSymbolicRef("tree", tree);

        if (tree != null) {
            if (tree.repOKComplete()) {
                // Ground Truth Positive
                if (tree.repOKSymSolve()) {
                    Peer.countTruePositive();
                } else {
                    Peer.countFalseNegative();
                }
            } else {
                // Ground Truth Negative
                if (tree.repOKSymSolve()) {
                    Peer.countFalsePositive();
                } else {
                    Peer.countTrueNegative();
                }
            }
        }

    }
}
