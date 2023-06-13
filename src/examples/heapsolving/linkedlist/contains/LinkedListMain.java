/*
 * @(#)LinkedList.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.linkedlist.contains;

import heapsolving.linkedlist.LinkedList;
import heapsolving.linkedlist.LinkedListHarness;
import pli.SymHeap;
import pli.TestGen;

public class LinkedListMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("contains", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        LinkedList structure = LinkedListHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.contains(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
