/*
 * @(#)HashMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.hashmap.containsvalue;

import heapsolving.hashmap.HashMap;
import heapsolving.hashmap.HashMapHarness;
import lissa.SymHeap;

public class HashMapMain {

    public static void main(String[] args) {
        Object value = new Object();

        HashMap structure = HashMapHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.containsValue(value);
            } catch (Exception e) {
                SymHeap.countException();
                System.out.println(e);
            }

            SymHeap.countPath();
        }
    }

}
