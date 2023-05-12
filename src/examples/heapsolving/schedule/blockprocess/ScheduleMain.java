/*
 * @(#)Schedule.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.schedule.blockprocess;

import heapsolving.schedule.Schedule;
import heapsolving.schedule.ScheduleHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class ScheduleMain {

    private static void registerTargetMethodData() {
        int numberOfArguments = 0;
        TestGen.registerTargetMethod("blockProcess", numberOfArguments);
    }

    public static void main(String[] args) {
        registerTargetMethodData();

        Schedule structure = ScheduleHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.blockProcess();
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
            assert(structure.repOKComplete());
        }
    }

}
