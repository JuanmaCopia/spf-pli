package lissa;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import lissa.heap.testgen.args.ConcreteArgument;
import lissa.heap.testgen.args.SymbolicIntegerArgument;
import lissa.heap.testgen.args.SymbolicStringArgument;

public class JPF_lissa_TestGen extends NativePeer {

    @MJI
    public static void registerConcreteArgument(MJIEnv env, int objRef, int nameRef, int codeRef) {
        String name = env.getStringObject(nameRef);
        String code = env.getStringObject(codeRef);

        ConcreteArgument arg = new ConcreteArgument(name, code);
        LISSAShell.solvingStrategy.setArgument(arg);

//        System.out.println(String.format("Registered concrete input: %s ", name));
    }

    @MJI
    public static void registerSymbolicIntegerArgument(MJIEnv env, int objRef, int symInt) {
        Object[] attrs = env.getArgAttributes();
        assert (attrs != null);
        assert (attrs[0] instanceof SymbolicInteger);
        SymbolicInteger symVar = (SymbolicInteger) attrs[0];
        assert (symVar != null);

        SymbolicIntegerArgument arg = new SymbolicIntegerArgument(symVar);
        LISSAShell.solvingStrategy.setArgument(arg);

//        System.out.println(String.format("Registered Symbolic Integer input: %s ", symVar.getName()));
    }

    @MJI
    public static void registerSymbolicStringArgument(MJIEnv env, int objRef, int symStringRef) {
        Object[] attrs = env.getArgAttributes();
        assert (attrs != null);
        assert (attrs[0] instanceof StringSymbolic);
        StringSymbolic symVar = (StringSymbolic) attrs[0];
        assert (symVar != null);

        SymbolicStringArgument arg = new SymbolicStringArgument(symVar);
        LISSAShell.solvingStrategy.setArgument(arg);

//        System.out.println(String.format("Registered Symbolic Integer input: %s ", symVar.getName()));
    }

    @MJI
    public static void registerTargetMethod(MJIEnv env, int objRef, int stringRef, int numArgs) {
        String methodName = env.getStringObject(stringRef);
        LISSAShell.solvingStrategy.setTargetMethod(methodName, numArgs);
//        System.out.println("Registered Target Method: " + methodName);
//        System.out.println("Number of Arguments: " + numArgs);
    }

}