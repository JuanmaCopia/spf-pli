/*
 * @(#)TreeSet.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.treeset.contains;

import heapsolving.treeset.TreeSet;
import heapsolving.treeset.TreeSetHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class TreeSetMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("contains", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        TreeSet structure = TreeSetHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.contains(key);
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
