/*
 * @(#)SQLFilterClauses.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.sqlfilterclauses.get;

import heapsolving.sqlfilterclauses.SQLFilterClauses;
import heapsolving.sqlfilterclauses.SQLFilterClausesHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class SQLFilterClausesMain {

    private static void registerTargetMethodData(int clause, int table) {
        int numberOfArguments = 2;
        TestGen.registerTargetMethod("get", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(clause);
        TestGen.registerSymbolicIntegerArgument(table);
    }

    public static void main(String[] args) {
        int clauseName = SymHeap.makeSymbolicInteger("INPUTclauseName");
        int tableName = SymHeap.makeSymbolicInteger("INPUTtableName");

        registerTargetMethodData(clauseName, tableName);

        SQLFilterClauses structure = SQLFilterClausesHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.get(clauseName, tableName);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            if (!structure.repOKComplete())
                SymHeap.repOKViolation();
            SymHeap.pathFinished();
        }
    }

}
