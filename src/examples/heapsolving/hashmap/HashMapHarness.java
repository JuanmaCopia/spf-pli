package heapsolving.hashmap;

import gov.nasa.jpf.vm.Verify;
import pli.SymHeap;

public class HashMapHarness {

    public static HashMap getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        HashMap structure = new HashMap();
        structure = (HashMap) SymHeap.makeSymbolicRefThis("hashmap_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.pre())
                return null;
        }

        return structure;
    }

    private static HashMap generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        HashMap structure = new HashMap();
        int numNodes = Verify.getInt(0, maxScope);
        for (int i = 1; i <= numNodes; i++) {
            structure.put(SymHeap.makeSymbolicInteger("N" + i), new Object());
        }
        return structure;
    }

}
