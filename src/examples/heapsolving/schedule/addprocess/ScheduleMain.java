/*
 * @(#)Schedule.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.schedule.addprocess;

import heapsolving.schedule.Schedule;
import heapsolving.schedule.ScheduleHarness;
import lissa.SymHeap;

public class ScheduleMain {

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        Schedule structure = ScheduleHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.addProcess(key);
            } catch (Exception e) {
                SymHeap.countException();
                System.out.println(e);
            }

            SymHeap.countPath();
        }
    }

}
