package heapsolving.avltree;

import gov.nasa.jpf.vm.Verify;
import pli.SymHeap;

public class AvlTreeHarness {

    public static AvlTree getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        AvlTree structure = new AvlTree();
        structure = (AvlTree) SymHeap.makeSymbolicRefThis("avltree_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.pre())
                return null;
        }

        return structure;
    }

    private static AvlTree generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        AvlTree tree = new AvlTree();
        int numNodes = Verify.getInt(0, maxScope);
        for (int i = 1; i <= numNodes; i++) {
            tree.insert(SymHeap.makeSymbolicInteger("N" + i));
        }
        return tree;
    }

}
