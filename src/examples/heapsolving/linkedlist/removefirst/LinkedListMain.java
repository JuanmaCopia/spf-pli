/*
 * @(#)LinkedList.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.linkedlist.removefirst;

import java.util.NoSuchElementException;

import heapsolving.linkedlist.LinkedList;
import heapsolving.linkedlist.LinkedListHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class LinkedListMain {

    private static void registerTargetMethodData() {
        int numberOfArguments = 0;
        TestGen.registerTargetMethod("removeFirst", numberOfArguments);
    }

    public static void main(String[] args) {
        registerTargetMethodData();

        LinkedList structure = LinkedListHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.removeFirst();
            } catch (NoSuchElementException e) {

            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
