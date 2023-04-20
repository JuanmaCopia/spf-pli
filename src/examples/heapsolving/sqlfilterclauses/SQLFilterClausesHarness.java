package heapsolving.sqlfilterclauses;

import gov.nasa.jpf.vm.Verify;
import lissa.SymHeap;

public class SQLFilterClausesHarness {

    public static SQLFilterClauses getStructure() {
        if (SymHeap.usingDriverStrategy())
            return generateDriverStructure();

        SQLFilterClauses structure = new SQLFilterClauses();
        structure = (SQLFilterClauses) SymHeap.makeSymbolicRefThis("sqlfilterclauses_0", structure);

        if (SymHeap.usingIfRepOKStrategy()) {
            if (!structure.repOKComplete())
                return null;
        }

        return structure;
    }

    private static SQLFilterClauses generateDriverStructure() {
        int maxScope = SymHeap.getMaxScope();
        SQLFilterClauses h = new SQLFilterClauses();
        int numNodes = Verify.getInt(0, maxScope);

        for (int i = 1; i <= numNodes; i++) {
            int clauseName = SymHeap.makeSymbolicInteger("N" + i);
            int tableName = SymHeap.makeSymbolicInteger("N2" + i);
            String clauseInformation = SymHeap.makeSymbolicString("N3" + i);
            try {
                h.put(clauseName, tableName, clauseInformation);
            } catch (Exception e) {
            }
        }
        return h;
    }

}
