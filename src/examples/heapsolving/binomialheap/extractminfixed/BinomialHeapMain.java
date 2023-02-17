/*
 * @(#)BinomialHeap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.binomialheap.extractminfixed;

import heapsolving.binomialheap.BinomialHeap;
import heapsolving.binomialheap.BinomialHeapHarness;
import lissa.SymHeap;

public class BinomialHeapMain {

    public static void main(String[] args) {
        BinomialHeap structure = BinomialHeapHarness.getStructure();
        if (structure != null) {
            try {
                structure.extractMinFixed();
            } catch (Exception e) {
                SymHeap.countException();
                System.out.println(e);
            }
            SymHeap.countPath();

            //assert (structure.countNodes() == structure.getSize());

//            if (structure.countNodes() != structure.getSize()) {
//                SymHeap.countException();
//                System.out.println("ERROR FOUND: POSTONDITION VIOLATED!!");
//            }
        }
    }

}