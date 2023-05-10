package heapsolving.template;

import gov.nasa.jpf.vm.Verify;
import lissa.SymHeap;

public class TemplateHarness {

    public static Template getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        Template structure = new Template();
        structure = (Template) SymHeap.makeSymbolicRefThis("treemap_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.repOKComplete())
                return null;
        }

        return structure;
    }

    private static Template generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        Template t = new Template();
        int numNodes = Verify.getInt(0, maxScope);
        for (int i = 1; i <= numNodes; i++) {
            int name = SymHeap.makeSymbolicInteger("pname" + i);
            int index = SymHeap.makeSymbolicInteger("pindex" + i);
            int row = SymHeap.makeSymbolicInteger("prow" + i);
            int col = SymHeap.makeSymbolicInteger("pcol" + i);
            t.addParameter(name, index, row, col);
        }
        return t;
    }

}
