/*
 * @(#)TransportStats.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.transportstats.byteswritten;

import heapsolving.transportstats.TransportStats;
import heapsolving.transportstats.TransportStatsHarness;
import pli.SymHeap;
import pli.TestGen;

public class TransportStatsMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("bytesWritten", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        TransportStats structure = TransportStatsHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.bytesWritten(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
