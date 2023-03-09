/*
 * @(#)Template.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package heapsolving.template.addparameter;

import heapsolving.template.Parameter;
import heapsolving.template.Template;
import heapsolving.template.TemplateHarness;
import lissa.SymHeap;

public class TemplateMain {

    public static void main(String[] args) {
        Parameter p = new Parameter();
        p.setName(SymHeap.makeSymbolicInteger("paramName"));
        p.setIndex(SymHeap.makeSymbolicInteger("paramIndex"));
        p.setRow(SymHeap.makeSymbolicInteger("paramRow"));
        p.setColumn(SymHeap.makeSymbolicInteger("paramCol"));

        Template structure = TemplateHarness.getStructure();
        if (structure != null) {
            try {
                // Call to method under analysis
                structure.addParameter(p);
            } catch (Exception e) {
                SymHeap.exceptionThrown();
                e.printStackTrace();
            }

            SymHeap.pathFinished();
        }
    }

}
