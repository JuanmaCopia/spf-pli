/*
 * @(#)HashMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.hashmap.put;

import heapsolving.hashmap.HashMap;
import heapsolving.hashmap.HashMapHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class HashMapMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 2;
        TestGen.registerTargetMethod("put", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
        TestGen.registerConcreteArgument("value", "Object value = new Object();");
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");
        Object value = new Object();

        registerTargetMethodData(key);

        HashMap structure = HashMapHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.put(key, value);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();
        }
    }

}
