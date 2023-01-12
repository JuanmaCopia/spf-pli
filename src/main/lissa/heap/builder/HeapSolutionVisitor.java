package lissa.heap.builder;

import java.util.HashMap;

import gov.nasa.jpf.symbc.numeric.Expression;
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
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.VM;
import lissa.heap.SymbolicReferenceInput;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;

public class HeapSolutionVisitor extends GenericCandidateVisitor {

    MJIEnv env;

    SymbolicReferenceInput symRefInput;

    ElementInfo newObjectRootElementInfo;
    Integer rootSymbolicInputIndex;

    ElementInfo currentObjectElementInfo;
    Integer currentObjectInSymRefInput;

    HashMap<Object, ElementInfo> symSolveToNewJPFObjects = new HashMap<>();
    HashMap<Object, Integer> symSolveToSymbolicInputObjects = new HashMap<>();

    int symbolicID = 0;

    public HeapSolutionVisitor(MJIEnv env, int newObjectRootRef, SymbolicReferenceInput symRefInput) {
        this.env = env;
        this.newObjectRootElementInfo = VM.getVM().getHeap().getModifiable(newObjectRootRef);
        this.symRefInput = symRefInput;
        this.rootSymbolicInputIndex = symRefInput.getRootHeapNode().getIndex();
    }

    @Override
    public void setRoot(Object rootObject, int rootID) {
        super.setRoot(rootObject, rootID);
        symSolveToNewJPFObjects.put(rootObject, newObjectRootElementInfo);
        symSolveToSymbolicInputObjects.put(rootObject, rootSymbolicInputIndex);
    }

    @Override
    public void setCurrentOwner(Object currentOwnerObject, int currentOwnerID) {
        super.setCurrentOwner(currentOwnerObject, currentOwnerID);
        currentObjectElementInfo = symSolveToNewJPFObjects.get(currentOwnerObject);
        assert (currentObjectElementInfo != null);

        // check
        currentObjectInSymRefInput = symSolveToSymbolicInputObjects.get(currentOwnerObject);
        assert (currentObjectInSymRefInput != null);
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo mirrorFieldObject = symSolveToNewJPFObjects.get(fieldObject);
        currentObjectElementInfo.setReferenceField(currentFieldName, mirrorFieldObject.getObjectRef());
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
        currentObjectElementInfo.setReferenceField(currentFieldName, MJIEnv.NULL);
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
//        System.out.println("Accesed new reference field: " + currentFieldName);
        ElementInfo newObjectElementInfo = env.newElementInfo(currentFieldClassName);
        currentObjectElementInfo.setReferenceField(currentFieldName, newObjectElementInfo.getObjectRef());
        symSolveToNewJPFObjects.put(fieldObject, newObjectElementInfo);

        ClassInfo clsInfo = currentObjectElementInfo.getClassInfo();
        FieldInfo field = clsInfo.getInstanceField(currentFieldName);

        Integer equivalentObjectInSymInput;

        if (currentObjectInSymRefInput == SymbolicReferenceInput.SYMBOLIC) {
            equivalentObjectInSymInput = SymbolicReferenceInput.SYMBOLIC;
        } else {
            equivalentObjectInSymInput = symRefInput.getReferenceField(currentObjectInSymRefInput, field);
            assert (equivalentObjectInSymInput != null);

            // if it is null there is an inconsistency: If the solution has a new object,
            // either it was
            // concrete on the symheap and had an object, or it was symbolic
            assert (equivalentObjectInSymInput != SymbolicReferenceInput.NULL);
        }

        symSolveToSymbolicInputObjects.put(fieldObject, equivalentObjectInSymInput);

    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
        assert (currentObjectInSymRefInput != SymbolicReferenceInput.NULL);

        ClassInfo clsInfo = currentObjectElementInfo.getClassInfo();
        FieldInfo field = clsInfo.getInstanceField(currentFieldName);

        Expression symbolicValue = null;
        if (currentObjectInSymRefInput != SymbolicReferenceInput.SYMBOLIC) {
            symbolicValue = symRefInput.getPrimitiveSymbolicField(currentObjectInSymRefInput, field);
            assert (symbolicValue != null);
        } else {
            String name = field.getName() + "(sym)_" + symbolicID;
            symbolicID++;
            if (field instanceof IntegerFieldInfo || field instanceof LongFieldInfo) {
                symbolicValue = new SymbolicInteger(name);
            } else if (field instanceof FloatFieldInfo || field instanceof DoubleFieldInfo) {
                symbolicValue = new SymbolicReal(name);
            } else if (field instanceof ReferenceFieldInfo) {
                if (field.getType().equals("java.lang.String"))
                    symbolicValue = new StringSymbolic(name);
                else
                    symbolicValue = new SymbolicInteger(name);
            } else if (field instanceof BooleanFieldInfo) {
                // treat boolean as an integer with range [0,1]
                symbolicValue = new SymbolicInteger(name, 0, 1);
            }
        }
        assert (symbolicValue != null);
        currentObjectElementInfo.setFieldAttr(field, symbolicValue);
    }
}
