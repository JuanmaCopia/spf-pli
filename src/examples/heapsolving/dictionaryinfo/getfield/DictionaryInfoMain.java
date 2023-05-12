/*
 * @(#)DictionaryInfo.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.dictionaryinfo.getfield;

import heapsolving.dictionaryinfo.DictionaryInfo;
import heapsolving.dictionaryinfo.DictionaryInfoHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class DictionaryInfoMain {

    private static void registerTargetMethodData(int tagNumber) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("getField", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(tagNumber);
    }

    public static void main(String[] args) {
        int tagNumber = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(tagNumber);

        DictionaryInfo structure = DictionaryInfoHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.getField(tagNumber);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
            if (!structure.repOKComplete())
                SymHeap.repOKViolation();
        }
    }

}
