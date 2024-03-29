/*
 * @(#)Schedule.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.schedule.quantumexpire;

import heapsolving.schedule.Schedule;
import heapsolving.schedule.ScheduleHarness;
import pli.SymHeap;
import pli.TestGen;

public class ScheduleMain {

    private static void registerTargetMethodData() {
        int numberOfArguments = 0;
        TestGen.registerTargetMethod("quantumExpire", numberOfArguments);
    }

    public static void main(String[] args) {
        registerTargetMethodData();

        Schedule structure = ScheduleHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.quantumExpire();
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
