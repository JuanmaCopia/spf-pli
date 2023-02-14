/*
 * @(#)AvlTree.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.avltree.remove;

import heapsolving.avltree.AvlTree;
import heapsolving.avltree.AvlTreeHarness;
import lissa.SymHeap;

public class AvlTreeMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        AvlTree structure = AvlTreeHarness.getStructure();
        if (structure != null) {
            try {
                structure.remove(key);
            } catch (Exception e) {
                SymHeap.countException();
                System.out.println(e);
            }
            SymHeap.countPath();
        }
    }

}
