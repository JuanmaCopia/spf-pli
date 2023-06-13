/*
 * @(#)HashMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.combatantstatistic.ensuretypexists;

import heapsolving.combatantstatistic.CombatantStatistic;
import heapsolving.combatantstatistic.CombatantStatisticHarness;
import pli.SymHeap;
import pli.TestGen;

public class CombatantStatisticMain {

    private static void registerTargetMethodData(int type) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("ensureTypExists", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(type);
    }

    public static void main(String[] args) {
        int type = SymHeap.makeSymbolicInteger("type");

        registerTargetMethodData(type);

        CombatantStatistic structure = CombatantStatisticHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.ensureTypExists(type);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
