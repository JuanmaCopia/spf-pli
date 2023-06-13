/*
 * @(#)TreeSet.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.treeset.add;

import heapsolving.treeset.TreeSet;
import heapsolving.treeset.TreeSetHarness;
import pli.SymHeap;
import pli.TestGen;

public class TreeSetMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("add", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        TreeSet structure = TreeSetHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.add(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
