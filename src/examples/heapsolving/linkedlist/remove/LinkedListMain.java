/*
 * @(#)LinkedList.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.linkedlist.remove;

import heapsolving.linkedlist.LinkedList;
import heapsolving.linkedlist.LinkedListHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class LinkedListMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("remove", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        LinkedList structure = LinkedListHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.remove(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
            assert(structure.repOKComplete());
        }
    }

}
