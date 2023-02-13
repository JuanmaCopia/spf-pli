package heapsolving.binomialheap;

import gov.nasa.jpf.vm.Verify;
import lissa.SymHeap;

public class BinomialHeapHarness {

    public static BinomialHeap getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        SymHeap.initializePathCondition();
        BinomialHeap structure = new BinomialHeap();
        structure = (BinomialHeap) SymHeap.makeSymbolicRefThis("binomialheap_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.repOKComplete())
                return null;
        }

        return structure;
    }

    private static BinomialHeap generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        BinomialHeap structure = new BinomialHeap();
        int numNodes = Verify.getInt(0, maxScope);
        for (int i = 1; i <= numNodes; i++) {
            structure.insert(SymHeap.makeSymbolicInteger("N" + i));
        }
        return structure;
    }

}
