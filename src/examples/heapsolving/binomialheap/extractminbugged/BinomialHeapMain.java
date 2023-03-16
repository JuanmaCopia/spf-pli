/*
 * @(#)BinomialHeap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.binomialheap.extractminbugged;

import heapsolving.binomialheap.BinomialHeap;
import heapsolving.binomialheap.BinomialHeapHarness;
import lissa.SymHeap;

public class BinomialHeapMain {

    public static void main(String[] args) {
        BinomialHeap structure = BinomialHeapHarness.getStructure();
        if (structure != null) {
            try {
                structure.extractMinBugged();
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();

//            if (structure.countNodes() != structure.getSize()) {
//                SymHeap.exceptionThrown();
//                System.out.println("ERROR FOUND: POSTONDITION VIOLATED!!");
//            }
        }
    }

}
