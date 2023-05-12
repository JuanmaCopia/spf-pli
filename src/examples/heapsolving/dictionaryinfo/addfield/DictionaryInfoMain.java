/*
 * @(#)DictionaryInfo.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.dictionaryinfo.addfield;

import heapsolving.dictionaryinfo.DictionaryInfo;
import heapsolving.dictionaryinfo.DictionaryInfoHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class DictionaryInfoMain {

    private static void registerTargetMethodData(int tagNumber, int fieldName) {
        int numberOfArguments = 2;
        TestGen.registerTargetMethod("addField", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(tagNumber);
        TestGen.registerSymbolicIntegerArgument(fieldName);
    }

    public static void main(String[] args) {
        int tagNumber = SymHeap.makeSymbolicInteger("tagNumber");
        int name = SymHeap.makeSymbolicInteger("fieldName");

        registerTargetMethodData(tagNumber, name);

        DictionaryInfo structure = DictionaryInfoHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.addField(tagNumber, name);
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
