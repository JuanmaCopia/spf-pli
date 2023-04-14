/*
 * @(#)HashMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.combatantstatistic.adddata;

import heapsolving.combatantstatistic.CombatantStatistic;
import heapsolving.combatantstatistic.CombatantStatisticHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class CombatantStatisticMain {

    private static void registerTargetMethodData(int type, int side, int value) {
        int numberOfArguments = 3;
        TestGen.registerTargetMethod("addData", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(type);
        TestGen.registerSymbolicIntegerArgument(side);
        TestGen.registerSymbolicIntegerArgument(value);
    }

    public static void main(String[] args) {
        int type = SymHeap.makeSymbolicInteger("type");
        int side = SymHeap.makeSymbolicInteger("side");
        int value = SymHeap.makeSymbolicInteger("value");

        registerTargetMethodData(type, side, value);

        CombatantStatistic structure = CombatantStatisticHarness.getStructure();
        if (structure != null) {
            try {
                SymHeap.assume(side >= 0 && side <= 1);
                SymHeap.assume(type >= 0 && type <= 14);
                // Call to method under analysis
                structure.addData(type, side, value);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
