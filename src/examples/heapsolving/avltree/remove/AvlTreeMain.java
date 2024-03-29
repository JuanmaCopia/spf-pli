/*
 * @(#)AvlTree.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.avltree.remove;

import heapsolving.avltree.AvlTree;
import heapsolving.avltree.AvlTreeHarness;
import pli.SymHeap;
import pli.TestGen;

public class AvlTreeMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("remove", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        AvlTree structure = AvlTreeHarness.getStructure();
        if (structure != null) {
            try {
                structure.remove(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();
        }
    }

}
