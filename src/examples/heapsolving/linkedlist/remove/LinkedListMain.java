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

public class LinkedListMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        LinkedList structure = LinkedListHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.remove(key);
            } catch (Exception e) {
                SymHeap.countException();
            }

            SymHeap.countPath();
            if (SymHeap.isCheckPathValidityEnabled())
                LinkedList.checkPathValidity(structure);
        }
    }

}
