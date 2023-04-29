package lissa.heap.testgen;

import java.util.List;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringPathCondition;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import lissa.LISSAShell;
import lissa.heap.SymHeapHelper;
import lissa.heap.testgen.args.SymbolicArgument;
import lissa.heap.testgen.args.SymbolicIntegerArgument;
import lissa.heap.testgen.args.SymbolicStringArgument;
import lissa.heap.testgen.args.TargetMethod;

public class TestCaseHelper {

    public static void solveTargetMethodArguments(PathCondition pc) {
        // System.out.println("\n\nPath Condition: \n" + pc.toString());
        TargetMethod method = LISSAShell.solvingStrategy.getTargetMethod();
        List<SymbolicArgument> symArgs = method.getSymbolicArguments();
        for (SymbolicArgument arg : symArgs) {
            solveArgument(arg, pc);
            // System.out.println(String.format("Argument %s solved to: %s", arg.getName(),
            // arg.getStringValue()));
        }
    }

    private static void solveArgument(SymbolicArgument symbolicArg, PathCondition pc) {
        if (symbolicArg instanceof SymbolicIntegerArgument) {
            SymbolicIntegerArgument symIntArg = (SymbolicIntegerArgument) symbolicArg;
            SymbolicInteger symVar = symIntArg.getSymbolicVariable();
            Integer solution = SymHeapHelper.getSolution(symVar, pc);
            symbolicArg.setValue(Integer.toString(solution));
        }
        if (symbolicArg instanceof SymbolicStringArgument) {
            StringPathCondition spc = null;
            if (pc != null) {
                spc = pc.spc;
                if (spc != null) {
                    spc.solve();
                    // System.out.println(pc.spc.toString());
                }
            }
            SymbolicStringArgument symStrArg = (SymbolicStringArgument) symbolicArg;
            StringSymbolic symVar = symStrArg.getSymbolicVariable();
            String solution = "";
            if (spc != null) {
                solution = symVar.solution();
                // System.out.println("solution: " + val);
                if (solution == StringSymbolic.UNDEFINED)
                    solution = "''";
            }
            symbolicArg.setValue(solution);
        }
    }

}