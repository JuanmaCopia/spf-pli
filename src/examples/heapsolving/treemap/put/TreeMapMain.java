/*
 * @(#)TreeMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.treemap.put;

import heapsolving.treemap.TreeMap;
import heapsolving.treemap.TreeMapHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class TreeMapMain {

    private static void registerTargetMethodData(int key, Object value) {
        int numberOfArguments = 2;
        TestGen.registerTargetMethod("put", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
        if (value == null)
            TestGen.registerConcreteArgument("value", "Object value = null;");
        else
            TestGen.registerConcreteArgument("value", "Object value = new Object();");
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");
        Object value = new Object();
        value = SymHeap.considerNullChoice(value);

        registerTargetMethodData(key, value);

        TreeMap structure = TreeMapHarness.getStructure();
        if (structure != null) {
            try {
                structure.put(key, value);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }
            SymHeap.pathFinished();
            if (!structure.repOKComplete())
                SymHeap.repOKViolation();
        }
    }

}
