/*
 * @(#)TreeMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.treemap.containskey;

import heapsolving.treemap.TreeMap;
import heapsolving.treemap.TreeMapHarness;
import lissa.SymHeap;

public class TreeMapMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        TreeMap structure = TreeMapHarness.getStructure();
        if (structure != null) {
            try {
                structure.containsKey(key);
            } catch (Exception e) {
                SymHeap.countException();
                System.out.println(e);
            }
            SymHeap.countPath();
            if (SymHeap.isCheckPathValidityEnabled())
                TreeMap.checkPathValidity(structure);
        }
    }

}
