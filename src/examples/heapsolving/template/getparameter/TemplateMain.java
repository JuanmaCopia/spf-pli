/*
 * @(#)Template.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.template.getparameter;

import heapsolving.template.Template;
import heapsolving.template.TemplateHarness;
import lissa.SymHeap;
import lissa.TestGen;

public class TemplateMain {

    private static void registerTargetMethodData(int key) {
        int numberOfArguments = 1;
        TestGen.registerTargetMethod("getParameter", numberOfArguments);
        TestGen.registerSymbolicIntegerArgument(key);
    }

    public static void main(String[] args) {
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        registerTargetMethodData(key);

        Template structure = TemplateHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.getParameter(key);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
            assert(structure.repOKComplete());
        }
    }

}
