/*
 * @(#)SQLFilterClauses.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.sqlfilterclauses.put;

import heapsolving.sqlfilterclauses.SQLFilterClauses;
import heapsolving.sqlfilterclauses.SQLFilterClausesHarness;
import pli.SymHeap;
import pli.TestGen;

public class SQLFilterClausesMain {

    private static void registerTargetMethodData(int clause, int table, String clauseInfo) {
        int numberOfArguments = 3;
        TestGen.registerTargetMethod("put", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(clause);
        TestGen.registerSymbolicIntegerArgument(table);
        TestGen.registerSymbolicStringArgument(clauseInfo);
    }

    public static void main(String[] args) {
        int clauseName = SymHeap.makeSymbolicInteger("INPUTclauseName");
        int tableName = SymHeap.makeSymbolicInteger("INPUTtableName");
        String clauseInformation = SymHeap.makeSymbolicString("INPUT_KEY3");

        registerTargetMethodData(clauseName, tableName, clauseInformation);

        SQLFilterClauses structure = SQLFilterClausesHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.put(clauseName, tableName, clauseInformation);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
