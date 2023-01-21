package lissa.heap.builder;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.BooleanSet;
import korat.finitization.impl.IntSet;
import korat.utils.IIntList;
import lissa.heap.SymbolicReferenceInput;

public class HeapSolutionVisitor2 extends HeapSolutionVisitor {

    IIntList accessedIndices;

    public HeapSolutionVisitor2(MJIEnv env, int newObjectRootRef, SymbolicReferenceInput symRefInput,
            IIntList accessedIndices) {
        super(env, newObjectRootRef, symRefInput);
        this.accessedIndices = accessedIndices;
    }

    @Override
    void setValueForExistingPrimitiveField() {
        assert (currentFieldDomain.isPrimitiveType());
        if (accessedIndices.contains(currentFieldIndexInVector)) {
            Class<?> clsOfField = currentFieldDomain.getClassOfField();
            if (clsOfField == int.class) {
                int value = ((IntSet) currentFieldDomain).getInt(currentFieldIndexInFieldDomain);
                currentObjectElementInfo.setIntField(currentField, value);
            } else if (clsOfField == boolean.class) {
                boolean value = ((BooleanSet) currentFieldDomain).getBoolean(currentFieldIndexInFieldDomain);
                currentObjectElementInfo.setBooleanField(currentField, value);
            } else {
                assert (false); // TODO: add support for other types, String, Long, etc.
            }
        } else {
            Expression symbolicValue = symRefInput.getPrimitiveSymbolicField(currentObjectInSymRefInput, currentField);
            assert (symbolicValue != null);
            currentObjectElementInfo.setFieldAttr(currentField, symbolicValue);
        }
    }

}
