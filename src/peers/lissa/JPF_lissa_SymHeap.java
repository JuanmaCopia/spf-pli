package lissa;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.MinMax;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.config.ConfigParser;
import lissa.config.SolvingStrategyEnum;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.cg.RepOKCallCG;
import lissa.heap.solving.techniques.LISSAPC;
import lissa.heap.solving.techniques.SolvingStrategy;

public class JPF_lissa_SymHeap extends NativePeer {

    @MJI
    public static void handleRepOKResult(MJIEnv env, int objRef, boolean result) {
        SystemState ss = env.getVM().getSystemState();
        // System.out.println("report result: " + result);
        if (result) {
            String cgID = "repOKCG";
            ChoiceGenerator<?> lastCG = ss.getChoiceGenerator();
            assert (lastCG != null);
            boolean found = false;
            for (ChoiceGenerator<?> cg = lastCG; cg != null; cg = cg.getPreviousChoiceGenerator()) {
                if (cgID.equals(cg.getId())) {
                    found = true;
                    RepOKCallCG rcg = (RepOKCallCG) cg;
                    rcg.result = true;
                    break;
                }
                cg.setDone();
            }
            assert (found);
        }
        ss.setIgnored(true);
    }

//  String cgID = "repOKCG";
//  RepOKCallCG repOKCG = env.getVM().getSystemState().getCurrentChoiceGenerator(cgID, RepOKCallCG.class);
//  assert (repOKCG != null && repOKCG instanceof RepOKCallCG);

    @MJI
    public static void buildSolutionHeap(MJIEnv env, int objRef, int objvRef) {
        if (objvRef == MJIEnv.NULL)
            throw new RuntimeException("## Error: null object");

        assert (LISSAShell.solvingStrategy instanceof LISSAPC);
        LISSAPC technique = (LISSAPC) LISSAShell.solvingStrategy;
        technique.buildSolutionHeap(env, objvRef);
    }

    @MJI
    public static void makeSymbolicImplicitInputThis(MJIEnv env, int objRef, int stringRef, int objvRef) {
        // makes all the fields of obj v symbolic and adds obj v to the symbolic heap to
        // kick off lazy initialization
        if (objvRef == MJIEnv.NULL)
            throw new RuntimeException("## Error: null object");
        // introduce a heap choice generator for the element in the heap
        ThreadInfo ti = env.getVM().getCurrentThread();
        SystemState ss = env.getVM().getSystemState();
        ChoiceGenerator<?> cg;

        if (!ti.isFirstStepInsn()) {
            cg = new HeapChoiceGenerator(1); // new
            ss.setNextChoiceGenerator(cg);
            env.repeatInvocation();
            return; // not used anyways
        }
        // else this is what really returns results

        cg = ss.getChoiceGenerator();
        assert (cg instanceof HeapChoiceGenerator) : "expected HeapChoiceGenerator, got: " + cg;

        // see if there were more inputs added before
        ChoiceGenerator<?> prevHeapCG = cg.getPreviousChoiceGenerator();
        while (!((prevHeapCG == null) || (prevHeapCG instanceof HeapChoiceGenerator))) {
            prevHeapCG = prevHeapCG.getPreviousChoiceGenerator();
        }

        PathCondition pcHeap;
        SymbolicInputHeapLISSA symInputHeap;
        if (prevHeapCG == null) {

            pcHeap = new PathCondition();
            symInputHeap = new SymbolicInputHeapLISSA();
        } else {
            pcHeap = ((HeapChoiceGenerator) prevHeapCG).getCurrentPCheap();
            symInputHeap = (SymbolicInputHeapLISSA) ((HeapChoiceGenerator) prevHeapCG).getCurrentSymInputHeap();
        }

        // set all the fields to be symbolic
        ClassInfo ci = env.getClassInfo(objvRef);
        FieldInfo[] fields = ci.getDeclaredInstanceFields();
        FieldInfo[] staticFields = ci.getDeclaredStaticFields();

        String name = env.getStringObject(stringRef);
        String refChain = name + "[" + objvRef + "]"; // why is the type used here? should use the name of the field
                                                      // instead

        SymbolicInteger newSymRef = new SymbolicInteger(refChain);
        // ElementInfo eiRef = DynamicArea.getHeap().get(objvRef);
        ElementInfo eiRef = VM.getVM().getHeap().getModifiable(objvRef);
        SymHeapHelper.initializeInstanceFields(fields, eiRef, refChain, symInputHeap);
        Helper.initializeStaticFields(staticFields, ci, ti);

        // create new HeapNode based on above info
        // update associated symbolic input heap

        ClassInfo typeClassInfo = eiRef.getClassInfo();

        HeapNode rootHeapNode = new HeapNode(objvRef, typeClassInfo, newSymRef);
        symInputHeap._add(rootHeapNode);
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        symRefInput.setRootHeapNode(rootHeapNode);

        pcHeap._addDet(Comparator.NE, newSymRef, new IntegerConstant(-1));
        ((HeapChoiceGenerator) cg).setCurrentPCheap(pcHeap);
        ((HeapChoiceGenerator) cg).setCurrentSymInputHeap(symInputHeap);

        return;
    }

    @MJI
    public static int getMaxScope(MJIEnv env, int objRef) {
        ConfigParser conf = LISSAShell.configParser;
        return Integer.valueOf(conf.finitizationArgs);
    }

    @MJI
    public static boolean usingDriverStrategy(MJIEnv env, int objRef) {
        return LISSAShell.configParser.solvingStrategy == SolvingStrategyEnum.DRIVER;
    }

    @MJI
    public static boolean usingLIHybridStrategy(MJIEnv env, int objRef) {
        return LISSAShell.configParser.solvingStrategy == SolvingStrategyEnum.LIHYBRID;
    }

    @MJI
    public static boolean usingIfRepOKStrategy(MJIEnv env, int objRef) {
        return LISSAShell.configParser.solvingStrategy == SolvingStrategyEnum.IFREPOK;
    }

    @MJI
    public static void countPath(MJIEnv env, int objRef) {
        VM vm = env.getVM();
        SolvingStrategy strategy = LISSAShell.solvingStrategy;
        strategy.pathFinished(vm, vm.getCurrentThread());
    }

    @MJI
    public static int makeSymbolicInteger(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, MinMax.getVarMinInt(name), MinMax.getVarMaxInt(name)));
        return 0;
    }

    @MJI
    public static long makeSymbolicLong(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, MinMax.getVarMinLong(name), MinMax.getVarMaxLong(name)));
        return 0;
    }

    @MJI
    public static short makeSymbolicShort(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, MinMax.getVarMinShort(name), MinMax.getVarMaxShort(name)));
        return 0;
    }

    @MJI
    public static byte makeSymbolicByte(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, MinMax.getVarMinByte(name), MinMax.getVarMaxByte(name)));
        return 0;
    }

    @MJI
    public static char makeSymbolicChar(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, MinMax.getVarMinChar(name), MinMax.getVarMaxChar(name)));
        return 0;
    }

    @MJI
    public static double makeSymbolicReal(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicReal(name, MinMax.getVarMinDouble(name), MinMax.getVarMaxDouble(name)));
        return 0.0;
    }

    @MJI
    public static boolean makeSymbolicBoolean(MJIEnv env, int objRef, int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new SymbolicInteger(name, 0, 1));
        return false;
    }

    @MJI
    public static int makeSymbolicString__Ljava_lang_String_2__Ljava_lang_String_2(MJIEnv env, int objRef,
            int stringRef) {
        String name = env.getStringObject(stringRef);
        env.setReturnAttribute(new StringSymbolic(name));
        return env.newString("WWWWW's Birthday is 12-17-77");
    }

}
