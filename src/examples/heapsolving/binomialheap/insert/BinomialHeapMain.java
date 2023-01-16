/*
 * @(#)BinomialHeap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.binomialheap.insert;

import heapsolving.binomialheap.BinomialHeap;
import heapsolving.binomialheap.BinomialHeapHarness;
import lissa.SymHeap;

public class BinomialHeapMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        BinomialHeap structure = BinomialHeapHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.insert(key);
            } catch (Exception e) {
            }
            SymHeap.countPath();
        }
    }

}