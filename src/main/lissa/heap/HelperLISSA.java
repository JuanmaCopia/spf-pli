/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lissa.heap;

import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.symbolicinput.SymbolicReferenceInput;


public class HelperLISSA {


    public static Expression initializeInstanceField(FieldInfo field, ElementInfo eiRef, String refChain, String suffix,
            SymbolicInputHeap symInputHeap) {
    	Expression sym_v = null;
		String name ="";

		name = field.getName();
		String fullName = refChain + "." + name + suffix;
		if (field instanceof IntegerFieldInfo || field instanceof LongFieldInfo) {
				sym_v = new SymbolicInteger(fullName);
		} else if (field instanceof FloatFieldInfo || field instanceof DoubleFieldInfo) {
			sym_v = new SymbolicReal(fullName);
		} else if (field instanceof ReferenceFieldInfo){
			if (field.getType().equals("java.lang.String"))
				sym_v = new StringSymbolic(fullName);
			else
				sym_v = new SymbolicInteger(fullName);
		} else if (field instanceof BooleanFieldInfo) {
				//	treat boolean as an integer with range [0,1]
				sym_v = new SymbolicInteger(fullName, 0, 1);
		}
		eiRef.setFieldAttr(field, sym_v);
		
		// ==== ADDED:
		
        SymbolicReferenceInput symRefInput = ((SymbolicInputHeapLISSA) symInputHeap).getImplicitInputThis();
        if (!(field instanceof ReferenceFieldInfo) || field.getType().equals("java.lang.String")) {
            symRefInput.addPrimitiveSymbolicField(eiRef.getObjectRef(), field.getName(), sym_v);
        } else {
            symRefInput.addReferenceField(eiRef.getObjectRef(), field.getName(), SymbolicReferenceInput.SYMBOLIC);
        }
        return sym_v;
    }

    public static void initializeInstanceFields(FieldInfo[] fields, ElementInfo eiRef, String refChain,
            SymbolicInputHeap symInputHeap) {
        for (int i = 0; i < fields.length; i++)
            initializeInstanceField(fields[i], eiRef, refChain, "", symInputHeap);
    }

    public static int addNewHeapNode(ClassInfo typeClassInfo, ThreadInfo ti, Object attr, PathCondition pcHeap,
            SymbolicInputHeap symInputHeap, int numSymRefs, HeapNode[] prevSymRefs, boolean setShared) {
        int daIndex = ti.getHeap().newObject(typeClassInfo, ti).getObjectRef();
        ti.getHeap().registerPinDown(daIndex);
        String refChain = ((SymbolicInteger) attr).getName(); // + "[" + daIndex + "]"; // do we really need to add
                                                              // daIndex here?
        SymbolicInteger newSymRef = new SymbolicInteger(refChain);
        ElementInfo eiRef = ti.getModifiableElementInfo(daIndex);// ti.getElementInfo(daIndex); // TODO to review!
        if (setShared) {
            eiRef.setShared(ti, true);// ??
        }
        // daIndex.getObjectRef() -> number

        // neha: this change allows all the fields in the class hierarchy of the
        // object to be initialized as symbolic and not just its instance fields

        int numOfFields = eiRef.getNumberOfFields();
        FieldInfo[] fields = new FieldInfo[numOfFields];
        for (int fieldIndex = 0; fieldIndex < numOfFields; fieldIndex++) {
            fields[fieldIndex] = eiRef.getFieldInfo(fieldIndex);
        }

        HelperLISSA.initializeInstanceFields(fields, eiRef, refChain, symInputHeap);

        // neha: this change allows all the static fields in the class hierarchy
        // of the object to be initialized as symbolic and not just its immediate
        // static fields
        ClassInfo superClass = typeClassInfo;
        while (superClass != null) {
            FieldInfo[] staticFields = superClass.getDeclaredStaticFields();
            Helper.initializeStaticFields(staticFields, superClass, ti);
            superClass = superClass.getSuperClass();
        }

        // Put symbolic array in PC if we create a new array.
        if (typeClassInfo.isArray()) {
            String typeClass = typeClassInfo.getType();
            ArrayExpression arrayAttr = null;
            if (typeClass.charAt(1) != 'L') {
                arrayAttr = new ArrayExpression(eiRef.toString());
            } else {
                arrayAttr = new ArrayExpression(eiRef.toString(), typeClass.substring(2, typeClass.length() - 1));
            }
            ti.getVM().getLastChoiceGeneratorOfType(PCChoiceGenerator.class).getCurrentPC().arrayExpressions
                    .put(eiRef.toString(), arrayAttr);
        }

        // create new HeapNode based on above info
        // update associated symbolic input heap
        HeapNode n = new HeapNode(daIndex, typeClassInfo, newSymRef);
        symInputHeap._add(n);
        pcHeap._addDet(Comparator.NE, newSymRef, new IntegerConstant(-1));
        pcHeap._addDet(Comparator.EQ, newSymRef, new IntegerConstant(numSymRefs));
        for (int i = 0; i < numSymRefs; i++)
            pcHeap._addDet(Comparator.NE, n.getSymbolic(), prevSymRefs[i].getSymbolic());
        return daIndex;
    }

}