package pli.heap.visitors.symbolicinput;

import java.util.HashMap;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import pli.heap.canonicalizer.VectorStructure;
import pli.numeric.CollectIntegerConstantsVisitor;

public class ConstantPropagationVisitor extends ReferenceFieldOnlyVisitor {

    HashMap<Expression, Integer> equalityConstants = new HashMap<Expression, Integer>();

    public ConstantPropagationVisitor(VectorStructure vector, PathCondition pc) {
        super(vector);
        if (pc != null) {
            CollectIntegerConstantsVisitor visitor = new CollectIntegerConstantsVisitor();
            // System.out.println("starting visit of PC: " + pc);
            pc.accept(visitor);

            this.equalityConstants = visitor.getEqulityConstants();
            // System.out.println("\n equality constants: " + this.equalityConstants);
        }

    }

    @Override
    public void visitedSymbolicPrimitiveField(Expression symbolicVar) {
        Integer value = equalityConstants.get(symbolicVar);
        if (value != null)
            vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, value);
        else
            vector.setPrimitiveFieldAsSymbolic(currentOwnerObjClassName, currentFieldName, symbolicVar);
    }

}