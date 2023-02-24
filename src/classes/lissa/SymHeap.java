package lissa;

import gov.nasa.jpf.vm.Verify;

public class SymHeap {

    native public static void initializePathCondition();

    public static Object makeSymbolicRefThis(String name, Object thisObject) {
        assert (thisObject != null);
        makeSymbolicImplicitInputThis(name, thisObject);
        return thisObject;
    }

    public static Object buildHeap(Object thisObject) {
        assert (thisObject != null);
        buildSolutionHeap(thisObject);
        return thisObject;
    }

    public static void assume(boolean c) {
        if (!c)
            Verify.ignoreIf(true);
    }

    native public static void handleRepOKResult(boolean result);

    native public static void handlePathCheckResult(boolean result);

    native public static void makeSymbolicImplicitInputThis(String name, Object v);

    native public static void buildSolutionHeap(Object v);

    native public static void buildPartialHeapInput(Object v);

    native public static int getMaxScope();

    native public static void countPath();

    native public static void countException();

    native public static boolean usingDriverStrategy();

    native public static boolean usingIfRepOKStrategy();

    native public static boolean usingLIHybridStrategy();

    native public static boolean usingSymSolveBasedStrategy();

    // Methods to create symbolic values:

    native public static int makeSymbolicInteger(String name);

    native public static long makeSymbolicLong(String name);

    native public static short makeSymbolicShort(String name);

    native public static byte makeSymbolicByte(String name);

    native public static double makeSymbolicReal(String name);

    native public static boolean makeSymbolicBoolean(String name);

    native public static char makeSymbolicChar(String name);

    native public static String makeSymbolicString(String name);

    // this method should be used instead of the native one in
    // the no-string-models branch of jpf-core
    public static String makeSymbolicString(String name, int size) {
        char str[] = new char[size];
        for (int i = 0; i < size; i++) {
            str[i] = makeSymbolicChar(name + i);
        }
        return new String(str);
    }

}
