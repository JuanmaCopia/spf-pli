/*
 * @(#)BinomialHeap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.binomialheap.extractminfixed;

import heapsolving.binomialheap.BinomialHeap;
import heapsolving.binomialheap.BinomialHeapHarness;
import pli.SymHeap;
import pli.TestGen;

public class BinomialHeapMain {

    private static void registerTargetMethodData() {
        TestGen.registerTargetMethod("extractMinFixed", 0);
    }

    public static void main(String[] args) {
        registerTargetMethodData();

        BinomialHeap structure = BinomialHeapHarness.getStructure();
        if (structure != null) {
            try {
                structure.extractMinFixed();
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();

            // assert (structure.countNodes() == structure.getSize());

//            if (structure.countNodes() != structure.getSize()) {
//                SymHeap.exceptionThrown();
//                System.out.println("ERROR FOUND: POSTONDITION VIOLATED!!");
//            }
        }
    }

}
