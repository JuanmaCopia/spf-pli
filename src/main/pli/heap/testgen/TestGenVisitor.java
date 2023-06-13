package pli.heap.testgen;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import pli.LISSAShell;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicReferenceInput.ObjectData;
import pli.heap.solving.techniques.LIBasedStrategy;
import pli.heap.solving.techniques.SolvingStrategy;
import pli.heap.testgen.args.Argument;
import pli.heap.testgen.args.TargetMethod;
import pli.heap.visitors.HeapVisitor;

public class TestGenVisitor implements HeapVisitor {

    Heap JPFHeap;

    PathCondition pc;

    StringBuilder testCase = new StringBuilder();

    // root data
    ElementInfo rootEI;
    ClassInfo rootClass;
    String rootClassName;
    String rootIdentifier;

    // owner data
    ObjectData currentOwnerData;

    // field data
    FieldInfo currentField;
    ClassInfo fieldClass;
    String currentRefChain;

    SolvingStrategy stg;

    public TestGenVisitor(Heap JPFHeap, PathCondition pc) {
        this.JPFHeap = JPFHeap;
        this.pc = pc;
        this.stg = LISSAShell.solvingStrategy;
    }

    @Override
    public void setRoot(int rootRef) {
        rootEI = JPFHeap.getModifiable(rootRef);
        rootClass = rootEI.getClassInfo();
        rootClassName = rootClass.getName();
        String simpleRootClassName = rootClass.getSimpleName();
        rootIdentifier = simpleRootClassName.toLowerCase() + "_0";
        appendTestSignature(rootClassName);
        appendDeclareAndCreateNewObject(rootClassName, rootIdentifier);
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
        appendTabbedLine(makeAssing(currentRefChain, "null"));
    }

    @Override
    public void visitedSymbolicReferenceField() {
        assert (false);
    }

    @Override
    public void visitedNewReferenceField(ObjectData fieldData) {
        appendTabbedLine(makeAssing(currentRefChain, makeConstructorCall(fieldClass.getName())));
    }

    @Override
    public void visitedExistentReferenceField(ObjectData fieldData) {
        appendTabbedLine(makeAssing(currentRefChain, fieldData.chainRef));
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
        } else if (currentField instanceof ReferenceFieldInfo) {
            if (currentField.getType().equals("java.lang.String")) {
                strValue = SymHeapHelper.getSolution((StringSymbolic) symbolicPrimitive, pc.spc);
            } else {
                assert (false);
            }
        } else {
            throw new RuntimeException("Unsuported type !!!!");
        }

        appendTabbedLine(makeAssing(currentRefChain, strValue));
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

        appendTabbedLine(makeAssing(currentRefChain, strValue));
    }

    @Override
    public void visitFinished() {
        appendLine("");
        appendTabbedLine(makeInputRepOKCheck() + "  // assert program precondition\n");
        testCase.append(createArgumentsCode());
        appendTabbedLine(makeMethodCallCode() + "  // SUT call\n");
        appendTabbedLine(makeInputRepOKCheck() + "  // assert postcondition");
        appendLine("}");
    }

    private String makeMethodCallCode() {
        TargetMethod method = stg.getTargetMethod();
        String methodName = method.getName();
        String arguments = createArgumentsString(method);
        return String.format("%s.%s(%s);", rootIdentifier, methodName, arguments);
    }

    private String createArgumentsString(TargetMethod method) {
        Argument[] args = method.getArguments();
        String commaSeparatedNames = "";
        if (args.length > 0) {
            commaSeparatedNames += args[0].getName();
            for (int i = 1; i < args.length; i++)
                commaSeparatedNames += ", " + args[i].getName();
        }
        return commaSeparatedNames;
    }

    private String createArgumentsCode() {
        TargetMethod method = stg.getTargetMethod();
        String code = "";
        for (int i = 0; i < method.getNumberOfArguments(); i++) {
            Argument arg = method.getArgument(i);
            code += "        " + arg.getDeclarationCode() + "\n";
        }
        return code;
    }

    String makeInputRepOKCheck() {
        return String.format("assertTrue(%s.repOKComplete());", rootIdentifier);
    }

    String makeSUTCall() {
        String methodName = "method";
        String args = "arg0, arg1";
        return String.format("%s.%s(%s));", rootIdentifier, methodName, args);
    }

    String makeConstructorCall(String className) {
        String constructor = className.replace("$", ".");
        return String.format("new %s()", constructor);
    }

    String makeAssing(String left, String right) {
        return String.format("%s = %s;", left, right);
    }

    void appendTestSignature(String className) {
        testCase.append(String.format("    @Test\n    public void %sTestTESTID() {\n",
                SolvingStrategy.config.targetMethodName));
    }

    void appendDeclareAndCreateNewObject(String className, String identifier) {
        String left = String.format("%s %s", className, identifier);
        String right = makeConstructorCall(className);
        appendTabbedLine(makeAssing(left, right));
    }

    void appendLine(String line) {
        testCase.append("    " + line + "\n");
    }

    void appendTabbedLine(String line) {
        testCase.append("        " + line + "\n");
    }

    @Override
    public boolean isIgnoredField() {
        String currentOwnerClassName = currentOwnerData.type.getName();
        LIBasedStrategy strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
        return !strategy.isFieldTracked(currentOwnerClassName, currentField.getName());
    }

    @Override
    public boolean isAborted() {
        return false;
    }

    public String getTestCaseCode() {
        return testCase.toString();
    }

}
