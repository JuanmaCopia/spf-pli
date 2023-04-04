package lissa.heap.testgen;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicReferenceInput.ObjectData;
import lissa.heap.visitors.HeapVisitor;

public class TestGenVisitor implements HeapVisitor {

    MJIEnv env;
    Heap JPFHeap;

    PathCondition pc;

    StringBuilder testCase = new StringBuilder();
    int testID;

    // root data
    ElementInfo rootEI;
    ClassInfo rootClass;
    String rootClassName;

    // owner data
    ObjectData currentOwnerData;

    // field data
    FieldInfo currentField;
    ClassInfo fieldClass;
    String currentRefChain;

    public TestGenVisitor(MJIEnv env, PathCondition pc, int testID) {
        this.env = env;
        this.pc = pc;
        this.JPFHeap = env.getVM().getHeap();
        this.testID = testID;
    }

    @Override
    public void setRoot(int rootRef) {
        rootEI = JPFHeap.getModifiable(rootRef);
        rootClass = rootEI.getClassInfo();
        rootClassName = rootClass.getSimpleName();
        appendTestSignature(rootClassName, testID);
        appendDeclareAndCreateNewObject(rootClassName, rootClassName.toLowerCase() + "_0");
    }

    @Override
    public void setCurrentOwner(ObjectData ownerData) {
        currentOwnerData = ownerData;
    }

    @Override
    public void setCurrentField(FieldInfo field, ClassInfo type) {
        currentField = field;
        fieldClass = type;
        currentRefChain = currentOwnerData.chainRef + "." + field.getName();
    }

    @Override
    public void visitedNullReferenceField() {
        appendLine(makeAssing(currentRefChain, "null"));
    }

    @Override
    public void visitedSymbolicReferenceField() {
        assert (false);
    }

    @Override
    public void visitedNewReferenceField(ObjectData fieldData) {
        appendLine(makeAssing(currentRefChain, makeConstructorCall(fieldClass.getName())));
    }

    @Override
    public void visitedExistentReferenceField(ObjectData fieldData) {
        appendLine(makeAssing(currentRefChain, fieldData.chainRef));
    }

    @Override
    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive) {
        String strValue = null;
        if (currentField instanceof IntegerFieldInfo || currentField instanceof LongFieldInfo) {
            int solution = SymHeapHelper.getSolution((SymbolicInteger) symbolicPrimitive, pc);
            strValue = Integer.toString(solution);
        } else if (currentField instanceof BooleanFieldInfo) {
            int solution = SymHeapHelper.getSolution((SymbolicInteger) symbolicPrimitive, pc);
            if (solution == 0)
                strValue = "false";
            else
                strValue = "true";
        } else {
            throw new RuntimeException("Unsuported type !!!!");
        }

        appendLine(makeAssing(currentRefChain, strValue));
    }

    @Override
    public void visitedConcretePrimitiveField() {
        ElementInfo ownerEI = currentOwnerData.objEI;
        String strValue = null;
        if (currentField instanceof IntegerFieldInfo) {
            int value = ownerEI.getIntField(currentField);
            strValue = Integer.toString(value);
        } else if (currentField instanceof LongFieldInfo) {
            long value = ownerEI.getLongField(currentField);
            strValue = Long.toString(value);
        } else if (currentField instanceof FloatFieldInfo) {
            float value = ownerEI.getFloatField(currentField);
            strValue = Float.toString(value);
        } else if (currentField instanceof DoubleFieldInfo) {
            double value = ownerEI.getDoubleField(currentField);
            strValue = Double.toString(value);
        } else if (currentField instanceof ReferenceFieldInfo) {
            if (currentField.getType().equals("java.lang.String")) {
                strValue = ownerEI.getStringField(currentField.getName());
            } else {
                assert (false);
            }
        } else if (currentField instanceof BooleanFieldInfo) {
            boolean value = ownerEI.getBooleanField(currentField);
            strValue = Boolean.toString(value);
        } else {
            throw new RuntimeException("Unsuported type !!!!");
        }

        appendLine(makeAssing(currentRefChain, strValue));
    }

    @Override
    public void visitFinished() {
        appendLine("}");
    }

    String makeConstructorCall(String className) {
        return String.format("new %s()", className);
    }

    String makeAssing(String left, String right) {
        return String.format("%s = %s;", left, right);
    }

    void appendTestSignature(String className, int testId) {
        testCase.append(String.format("@Test\npublic void %sTest%d {\n", className.toLowerCase(), testId));
    }

    void appendDeclareAndCreateNewObject(String className, String identifier) {
        String left = String.format("%s %s", className, identifier);
        String right = makeConstructorCall(className);
        appendLine(makeAssing(left, right));
    }

    void appendLine(String line) {
        testCase.append(line + "\n");
    }

    @Override
    public boolean isIgnoredField() {
        return false;
    }

    @Override
    public boolean isAborted() {
        return false;
    }

}
