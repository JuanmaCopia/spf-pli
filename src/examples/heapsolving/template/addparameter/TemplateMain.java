/*
 * @(#)Template.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.template.addparameter;

import heapsolving.template.Template;
import heapsolving.template.TemplateHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class TemplateMain {

    private static void registerTargetMethodData(int name, int index, int row, int col) {
        int numberOfArguments = 4;
        TestGen.registerTargetMethod("addParameter", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(name);
        TestGen.registerSymbolicIntegerArgument(index);
        TestGen.registerSymbolicIntegerArgument(row);
        TestGen.registerSymbolicIntegerArgument(col);
    }

    public static void main(String[] args) {
        int name = SymHeap.makeSymbolicInteger("name");
        int index = SymHeap.makeSymbolicInteger("index");
        int row = SymHeap.makeSymbolicInteger("row");
        int col = SymHeap.makeSymbolicInteger("col");

        registerTargetMethodData(name, index, row, col);

        Template structure = TemplateHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.addParameter(name, index, row, col);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
