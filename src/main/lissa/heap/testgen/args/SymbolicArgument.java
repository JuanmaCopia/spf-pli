package lissa.heap.testgen.args;

import gov.nasa.jpf.symbc.numeric.Expression;

public abstract class SymbolicArgument extends Argument {

    Expression symVar;

    @Override
    public boolean isSymbolic() {
        return true;
    }

    public Expression getSymbolicVariable() {
        return symVar;
    }

}
