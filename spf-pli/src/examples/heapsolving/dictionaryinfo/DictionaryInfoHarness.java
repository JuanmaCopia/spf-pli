package heapsolving.dictionaryinfo;

import gov.nasa.jpf.vm.Verify;
import pli.SymHeap;

public class DictionaryInfoHarness {

    public static DictionaryInfo getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        DictionaryInfo structure = new DictionaryInfo();
        structure = (DictionaryInfo) SymHeap.makeSymbolicRefThis("dictionaryinfo_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.repOKComplete())
                return null;
        }

        return structure;
    }

    public static DictionaryInfo generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        DictionaryInfo structure = new DictionaryInfo();

        int numNodes = Verify.getInt(0, maxScope);
        for (int i = 1; i <= numNodes; i++) {
            FieldInfo fi = new FieldInfo();
            fi.setTagNumber(SymHeap.makeSymbolicInteger("tagNum" + i));
            fi.setName(SymHeap.makeSymbolicInteger("name" + i));
            structure.addField(fi);
        }
        return structure;
    }

}
