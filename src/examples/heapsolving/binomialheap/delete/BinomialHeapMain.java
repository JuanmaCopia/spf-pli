
package heapsolving.binomialheap.delete;

import heapsolving.binomialheap.BinomialHeap;
import heapsolving.binomialheap.BinomialHeapHarness;
import lissa.SymHeap;

public class BinomialHeapMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        BinomialHeap structure = BinomialHeapHarness.getStructure();
        if (structure != null) {
            try {
                structure.delete(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();
        }
    }

}
