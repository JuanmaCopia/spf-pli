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

    ClassInfo currentObjectClass;

    FieldInfo currentField;

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
        currentObjectClass = currentObjectElementInfo.getClassInfo();

        // check
        currentObjectInSymRefInput = symSolveToSymbolicInputObjects.get(currentOwnerObject);
        assert (currentObjectInSymRefInput != null);
    }

    @Override
    public void setCurrentField(String clsOfFieldName, String fieldName, int fieldIndexInVector) {
        super.setCurrentField(clsOfFieldName, fieldName, fieldIndexInVector);
        currentField = currentObjectClass.getInstanceField(currentFieldName);
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo mirrorFieldObject = symSolveToNewJPFObjects.get(fieldObject);
        currentObjectElementInfo.setReferenceField(currentField, mirrorFieldObject.getObjectRef());
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
        currentObjectElementInfo.setReferenceField(currentField, MJIEnv.NULL);
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
//        System.out.println("Accesed new reference field: " + currentFieldName);
        ElementInfo newObjectElementInfo = env.newElementInfo(currentFieldClassName);
        currentObjectElementInfo.setReferenceField(currentField, newObjectElementInfo.getObjectRef());
        symSolveToNewJPFObjects.put(fieldObject, newObjectElementInfo);

        Integer equivalentObjectInSymInput;

        if (currentObjectInSymRefInput == SymbolicReferenceInput.SYMBOLIC) {
            equivalentObjectInSymInput = SymbolicReferenceInput.SYMBOLIC;
        } else {
            equivalentObjectInSymInput = symRefInput.getReferenceField(currentObjectInSymRefInput, currentField);
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

        if (currentObjectInSymRefInput != SymbolicReferenceInput.SYMBOLIC) {
            setValueForExistingPrimitiveField();
        } else {
            setValueForNonExistingPrimitiveField();
        }
    }

    void setValueForExistingPrimitiveField() {
        Expression symbolicValue = symRefInput.getPrimitiveSymbolicField(currentObjectInSymRefInput, currentField);
        assert (symbolicValue != null);
        currentObjectElementInfo.setFieldAttr(currentField, symbolicValue);
    }

    void setValueForNonExistingPrimitiveField() {
        Expression symbolicValue = null;
        String name = currentField.getName() + "(sym)_" + symbolicID;
        symbolicID++;
        if (currentField instanceof IntegerFieldInfo || currentField instanceof LongFieldInfo) {
            symbolicValue = new SymbolicInteger(name);
        } else if (currentField instanceof FloatFieldInfo || currentField instanceof DoubleFieldInfo) {
            symbolicValue = new SymbolicReal(name);
        } else if (currentField instanceof ReferenceFieldInfo) {
            if (currentField.getType().equals("java.lang.String"))
                symbolicValue = new StringSymbolic(name);
            else
                symbolicValue = new SymbolicInteger(name);
        } else if (currentField instanceof BooleanFieldInfo) {
            // treat boolean as an integer with range [0,1]
            symbolicValue = new SymbolicInteger(name, 0, 1);
        } else {
            throw new RuntimeException("symbolicValue is null !!!!");
        }
        currentObjectElementInfo.setFieldAttr(currentField, symbolicValue);
    }
}
