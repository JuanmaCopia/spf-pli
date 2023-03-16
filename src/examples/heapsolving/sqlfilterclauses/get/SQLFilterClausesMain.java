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

public class SQLFilterClausesMain {

    public static void main(String[] args) {
        int clauseName = SymHeap.makeSymbolicInteger("INPUTclauseName");
        int tableName = SymHeap.makeSymbolicInteger("INPUTtableName");

        SQLFilterClauses structure = SQLFilterClausesHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.get(clauseName, tableName);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
