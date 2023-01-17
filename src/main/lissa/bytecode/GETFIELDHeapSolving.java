package lissa.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.LISSAShell;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.solving.techniques.LIBasedStrategy;
import lissa.heap.solving.techniques.LIHYBRID;
import lissa.heap.solving.techniques.SolvingStrategy;

public class GETFIELDHeapSolving extends gov.nasa.jpf.jvm.bytecode.GETFIELD {
    public GETFIELDHeapSolving(String fieldName, String clsName, String fieldDescriptor) {
        super(fieldName, clsName, fieldDescriptor);
    }

    // private int numNewRefs = 0; // # of new reference objects to account for
    // polymorphism -- work of Neha Rungta -- needs to be updated

    boolean abstractClass = false;

    @Override
    public Instruction execute(ThreadInfo ti) {

        // ================ Modification Begin ================ //
        SolvingStrategy solvingStrategy = LISSAShell.solvingStrategy;
        assert (solvingStrategy instanceof LIBasedStrategy);
        LIBasedStrategy heapSolvingStrategy = (LIBasedStrategy) solvingStrategy;
        // ==================================================== //

        HeapNode[] prevSymRefs = null; // previously initialized objects of same type: candidates for lazy init
        int numSymRefs = 0; // # of prev. initialized objects
        ChoiceGenerator<?> prevHeapCG = null;

        StackFrame frame = ti.getModifiableTopFrame();
        int objRef = frame.peek(); // don't pop yet, we might re-enter
        lastThis = objRef;
        if (objRef == MJIEnv.NULL) {
            return ti.createAndThrowException("java.lang.NullPointerException",
                    "referencing field '" + fname + "' on null object");
        }

        ElementInfo ei = ti.getModifiableElementInfo(objRef); // getModifiableElementInfoWithUpdatedSharedness(objRef);
                                                              // POR broken
        FieldInfo fi = getFieldInfo();
        if (fi == null) {
            return ti.createAndThrowException("java.lang.NoSuchFieldError",
                    "referencing field '" + fname + "' in " + ei);
        }

        // System.out.println("\nObject: " + ei.toString() + " field: " + fi.getName());

        Object attr = ei.getFieldAttr(fi);
        // check if the field is of ref type & it is symbolic (i.e. it has an attribute)
        // if it is we need to do lazy initialization

        // ================ Modification Begin ================ //
        if (heapSolvingStrategy instanceof LIHYBRID && ((LIHYBRID) heapSolvingStrategy).reachedGETFIELDLimit(objRef)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return this;
        }

        ClassInfo typeClassInfo = fi.getTypeClassInfo(); // use this instead of fullType
        String fullClassName = typeClassInfo.getName();

        if (!fi.isReference() || attr == null || attr instanceof StringExpression
                || attr instanceof SymbolicStringBuilder || !heapSolvingStrategy.isClassInBounds(fullClassName)

        ) {
            return super.execute(ti);
        }
        // ================ Modification End ================ //

        // Lazy initialization:

        int currentChoice;
        ChoiceGenerator<?> thisHeapCG;

        if (!ti.isFirstStepInsn()) {
            prevSymRefs = null;
            numSymRefs = 0;

            prevHeapCG = ti.getVM().getSystemState().getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
            // to check if this still works in the case of cascaded choices...

            if (prevHeapCG != null) {
                // determine # of candidates for lazy initialization
                SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) ((HeapChoiceGenerator) prevHeapCG)
                        .getCurrentSymInputHeap();
                prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
                numSymRefs = prevSymRefs.length;
            }
            int increment = 2;
            if (typeClassInfo.isAbstract()) {
                abstractClass = true;
                increment = 1; // only null
            }

            thisHeapCG = new HeapChoiceGenerator(numSymRefs + increment); // +null,new
            ti.getVM().getSystemState().setNextChoiceGenerator(thisHeapCG);
            // ti.reExecuteInstruction();
            if (SymbolicInstructionFactory.debugMode)
                System.out.println("# heap cg registered: " + thisHeapCG);
            return this;

        } else { // this is what really returns results
            // here we can have 2 choice generators: thread and heappc at the same time?

            frame.pop(); // Ok, now we can remove the object ref from the stack

            thisHeapCG = ti.getVM().getSystemState().getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
            assert (thisHeapCG != null
                    && thisHeapCG instanceof HeapChoiceGenerator) : "expected HeapChoiceGenerator, got: " + thisHeapCG;
            currentChoice = ((HeapChoiceGenerator) thisHeapCG).getNextChoice();
        }

        PathCondition pcHeap; // this pc contains only the constraints on the heap
        SymbolicInputHeapLISSA symInputHeap;

        // depending on the currentChoice, we set the current field to an object that
        // was already created
        // 0 .. numymRefs -1, or to null or to a new object of the respective type,
        // where we set all its
        // fields to be symbolic

        prevHeapCG = thisHeapCG.getPreviousChoiceGeneratorOfType(HeapChoiceGenerator.class);

        if (prevHeapCG == null) {
            pcHeap = new PathCondition();
            symInputHeap = new SymbolicInputHeapLISSA();
        } else {
            pcHeap = ((HeapChoiceGenerator) prevHeapCG).getCurrentPCheap();
            symInputHeap = (SymbolicInputHeapLISSA) ((HeapChoiceGenerator) prevHeapCG).getCurrentSymInputHeap();
        }

        assert pcHeap != null;
        assert symInputHeap != null;

        prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
        numSymRefs = prevSymRefs.length;

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        int daIndex = 0; // index into JPF's dynamic area
        if (currentChoice < numSymRefs) { // lazy initialization using a previously lazily initialized object
            HeapNode candidateNode = prevSymRefs[currentChoice];
            // here we should update pcHeap with the constraint attr == candidateNode.sym_v
            pcHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, candidateNode.getSymbolic());
            daIndex = candidateNode.getIndex();
            // ================ Modification Begin ================ //
            symRefInput.addReferenceField(objRef, fi, daIndex);
            // ================ Modification End ================ //
        } else if (currentChoice == numSymRefs) { // null object
            pcHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, new IntegerConstant(-1));
            daIndex = MJIEnv.NULL;

            // ================ Modification Begin ================ //
            symRefInput.addReferenceField(objRef, fi, SymbolicReferenceInput.NULL);
            // ================ Modification End ================ //
        } else if (currentChoice == (numSymRefs + 1) && !abstractClass) {
            // creates a new object with all fields symbolic and adds the object to
            // SymbolicHeap

            // ================ Modification Begin ================ //
            Integer bound = heapSolvingStrategy.getBoundForClass(fullClassName);

            // backtrack if the max bound of nodes has been reached
            if (numSymRefs == bound) {
                ti.getVM().getSystemState().setIgnored(true);
                return this;
            }
            // ================ Modification End ================ //

            daIndex = SymHeapHelper.addNewHeapNode(typeClassInfo, ti, attr, pcHeap, symInputHeap, numSymRefs,
                    prevSymRefs, ei.isShared());

            // ================ Modification Begin ================ //
            symRefInput.addReferenceField(objRef, fi, daIndex);
            // ================ Modification End ================ //
        } else {
            System.err.println("subtyping not handled");
        }

        ei.setReferenceField(fi, daIndex);
        ei.setFieldAttr(fi, null);

        frame.pushRef(daIndex);

        ((HeapChoiceGenerator) thisHeapCG).setCurrentPCheap(pcHeap);
        ((HeapChoiceGenerator) thisHeapCG).setCurrentSymInputHeap(symInputHeap);
        if (SymbolicInstructionFactory.debugMode)
            System.out.println("GETFIELD pcHeap: " + pcHeap);

        // ================ Modification Begin ================ //
        if (!heapSolvingStrategy.checkHeapSatisfiability(ti, symInputHeap)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return this;
        }
        // ================= Modification End ================= //

        return heapSolvingStrategy.getNextInstructionToGETFIELD(ti, this, symInputHeap);
    }
}

// System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." +
// fi.getName());
// System.out.println("GETFIELD: Instruction Index: " + insnIndex);

//        boolean mustInvokeRepOK;
//        RepOKCallCG repOKCG;
//        String cgID = "repOKCG";
//
//        if (!ti.isFirstStepInsn()) {
//            repOKCG = new RepOKCallCG(cgID, 2); // invoke repok and dont invoke but recover result
//            ti.getVM().getSystemState().setNextChoiceGenerator(repOKCG);
//
//            System.out.println("# Repok CG registered: " + repOKCG);
//            return this;
//
//        }
//
//        repOKCG = ti.getVM().getSystemState().getCurrentChoiceGenerator(cgID, RepOKCallCG.class);
//        assert (repOKCG != null && thisHeapCG instanceof RepOKCallCG);
//        mustInvokeRepOK = repOKCG.getNextChoice() == 0;
//
//        if (mustInvokeRepOK)
//            return createInvokeRepOKInstruction(ti, symRefInput);
//
//        return getNext(ti);

// ====================== WORKING: STATIC METHOD CALL V3 ======================

/*
 * System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." +
 * fi.getName()); System.out.println("GETFIELD: Instruction Index: " +
 * insnIndex);
 *
 * int rootIndex = symRefInput.getRootHeapNode().getIndex(); ClassInfo
 * rootClassInfo = symRefInput.getRootHeapNode().getType(); MethodInfo repokMI =
 * rootClassInfo.getMethod("emptyMethodStatic()V", false);
 *
 * assert (repokMI != null);
 *
 * String clsName = repokMI.getClassInfo().getName(); String mthName =
 * repokMI.getName(); String signature = repokMI.getSignature();
 *
 * Instruction realInvoke = new INVOKESTATIC(clsName, mthName, signature);
 * realInvoke.setMethodInfo(this.getMethodInfo());
 * realInvoke.setLocation(this.insnIndex, this.position);
 *
 * Object[] args = null; Object[] attrs = null; pushArguments(ti, args, attrs);
 *
 * return realInvoke;
 */

// ====================== WORKING: STATIC METHOD CALL ====================== //

// ====================== WORKING: STATIC METHOD CALL V2 ======================
// //

/*
 * System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." +
 * fi.getName()); System.out.println("GETFIELD: Instruction Index: " +
 * insnIndex);
 *
 * int rootIndex = symRefInput.getRootHeapNode().getIndex(); ClassInfo
 * rootClassInfo = symRefInput.getRootHeapNode().getType(); MethodInfo repokMI =
 * rootClassInfo.getMethod("emptyMethodStatic()V", false);
 *
 * assert (repokMI != null);
 *
 * Object[] args = null; Invocation repokCall = new Invocation(repokMI, args,
 * null); LinkedList<Invocation> invList = new LinkedList<>();
 * invList.add(repokCall);
 *
 * INVOKECG realInvoke = new INVOKECG(invList);
 *
 * MethodInfo thisMI = this.getMethodInfo(); realInvoke.setMethodInfo(thisMI);
 * realInvoke.setLocation(this.insnIndex, this.position);
 *
 * return realInvoke;
 */

// ====================== WORKING: STATIC METHOD CALL ====================== //

// ====================== WORKING: STATIC METHOD CALL ====================== //

/*
 *
 * System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." +
 * fi.getName()); System.out.println("GETFIELD: Instruction Index: " +
 * insnIndex);
 *
 * int rootIndex = symRefInput.getRootHeapNode().getIndex(); ClassInfo
 * rootClassInfo = symRefInput.getRootHeapNode().getType(); MethodInfo repokMI =
 * rootClassInfo.getMethod("emptyMethodStatic()V", false);
 *
 * assert (repokMI != null);
 *
 * Invocation repokCall = new Invocation(repokMI, null, null);
 * LinkedList<Invocation> invList = new LinkedList<>(); invList.add(repokCall);
 *
 * INVOKECG realInvoke = new INVOKECG(invList);
 *
 * MethodInfo thisMI = this.getMethodInfo(); realInvoke.setMethodInfo(thisMI);
 * realInvoke.insnIndex = this.insnIndex;
 *
 * return realInvoke;
 *
 */

// ====================== WORKING: STATIC METHOD CALL ====================== //

//    System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." + fi.getName());
//
//    int rootIndex = symRefInput.getRootHeapNode().getIndex();
//    ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
//    MethodInfo repokMI = rootClassInfo.getMethod("emptyMethodStatic()V", false);
//
//    assert (repokMI != null);
//
//    String clsName = repokMI.getClassInfo().getName();
//    String mthName = repokMI.getName();
//    String signature = repokMI.getSignature();
//
//    STATICREPOK realInvoke = new STATICREPOK(clsName, mthName, signature);
//    realInvoke.setMethodInfo(repokMI);
//    return realInvoke;

//    System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." + fi.getName());
//    // HeapSolvingInstructionFactory.executingRepOK = true;
//
//    int rootIndex = symRefInput.getRootHeapNode().getIndex();
//    ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
//    MethodInfo repokMI = rootClassInfo.getMethod("emptyMethod()V", false);
//
//    String clsName = repokMI.getClassInfo().getName();
//    String mthName = repokMI.getName();
//    String signature = repokMI.getSignature();
//
//    INVOKEREPOK2 realInvoke = new INVOKEREPOK2(clsName, mthName, signature);
//
//    int position = this.getPosition() + 3;
//    int insIndex = this.getInstructionIndex() + 1;
//    realInvoke.setMethodInfo(repokMI);
//    realInvoke.setLocation(insIndex, position);
//
//    StackFrame f = ti.getModifiableTopFrame();
//    // frame.pushLocal(rootIndex);
//    f.push(rootIndex);
//
//    return realInvoke;

//        System.out.println("GETFIELD: " + ei.getClassInfo().getName() + "." + fi.getName());
//        HeapSolvingInstructionFactory.executingRepOK = true;
//
//        int rootIndex = symRefInput.getRootHeapNode().getIndex();
//        ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
//        MethodInfo repokMI = rootClassInfo.getMethod("emptyMethod()V", false);
//
//        String clsName = repokMI.getClassInfo().getName();
//        String mthName = repokMI.getName();
//        String signature = repokMI.getSignature();
//
////        JVMInstructionFactory insnFactory = JVMInstructionFactory.getFactory();
////        Instruction realInvoke = insnFactory.invokevirtual(clsName, mthName, signature);
//
//        Instruction realInvoke = HeapSolvingInstructionFactory.invokerepok2(clsName, mthName, signature);
//
//        int position = this.getPosition() + 3;
//        int insIndex = this.getInstructionIndex() + 1;
//
//        realInvoke.setMethodInfo(repokMI);
//        realInvoke.setLocation(insIndex, position);
//
//        StackFrame f = ti.getModifiableTopFrame();
//        // frame.pushLocal(rootIndex);
//        f.push(rootIndex);
//
//        return realInvoke;

//        HeapNode rootHeapNode = symRefInput.getRootHeapNode();
//        int rootIndex = rootHeapNode.getIndex();
//        ClassInfo rootClassInfo = rootHeapNode.getType();
//        Instruction myIns = HeapSolvingInstructionFactory.createInvokeVirtualIns("heapsolving.treemap.TreeMap",
//                "emptyMethod", "()V");
// HeapSolvingInstructionFactory.executingRepOK = true;
//        MethodInfo repokMI = rootClassInfo.getMethod("emptyMethod()V", false);
//        myIns.setMethodInfo(repokMI);
//        ((INVOKEREPOK) myIns).next = getNext(ti);
//        ((INVOKEREPOK) myIns).rootIndex = rootIndex;

// return new INVREPOK();

// ========== direct call repok
//
//        int rootIndex = symRefInput.getRootHeapNode().getIndex();
//        executeRepOK(ti, rootIndex);
//        return getNext(ti);

// ================ Modification End ================ //
//        return getNext(ti);

//    public void invokevirtual(String clsName, String methodName, String methodSignature) {
//        add(insnFactory.invokevirtual(clsName, methodName, methodSignature));
//        pc += 3;
//    }
//
//    protected void add(Instruction insn) {
//        insn.setMethodInfo(mi);
//        insn.setLocation(idx++, pc);
//        code.add(insn);
//    }

//    public void executeRepOK(ThreadInfo ti, int rootIndex) {
//        MJIEnv env = ti.getEnv();
//
//        System.out.println("# entering emptyMethod");
//        MethodInfo repokMI = env.getClassInfo(rootIndex).getMethod("emptyMethod()V", false);
//
//        DirectCallStackFrame frame = repokMI.createDirectCallStackFrame(ti, 0);
//        // DirectCallStackFrame frame = repokMI.createRunStartStackFrame(ti);
//
//        int argOffset = frame.setReferenceArgument(0, rootIndex, null);
//        // frame.setArgument( argOffset, a, null);
//        // frame.setFireWall();
//
//        try {
//            executeMethodHidden(ti, frame);
//            // ti.executeMethodHidden(frame);
//            // ti.advancePC();
//
//        } catch (UncaughtException ux) { // frame's method is firewalled
//            System.out.println("# hidden method execution failed: " + ux);
//            ti.clearPendingException();
//            ti.popFrame(); // this is still the DirectCallStackFrame, and we want to continue execution
//            // return -1;
//        }
//
//        // get the return value from the (already popped) frame
//        // int res = frame.getResult();
//
//        System.out.println("# exit emptyMethod");
//        // return res;
//    }
//
//    /**
//     * enter method atomically, but also hide it from listeners and do NOT add
//     * executed instructions to the path.
//     *
//     * this can be even more confusing than executeMethodAtomic(), since nothing
//     * prevents such a method from changing the program state, and we wouldn't know
//     * for what reason by looking at the trace
//     *
//     * this method should only be used if we have to enter test application code
//     * like hashCode() or equals() from native code, e.g. to silently check property
//     * violations
//     *
//     * executeMethodHidden also acts as an exception firewall, since we don't want
//     * any silently executed code fall back into the visible path (for no observable
//     * reason)
//     */
//    public void executeMethodHidden(ThreadInfo ti, StackFrame frame) {
//        System.out.println("enter executeMethodHidden");
//        ti.pushFrame(frame);
//
//        int depth = ti.countStackFrames(); // this includes the DirectCallStackFrame
//        Instruction pc = frame.getPC();
//
//        VM vm = ti.getVM();
//        // vm.getSystemState().incAtomic(); // to shut off avoidable context switches
//        // (MONITOR_ENTER and wait() can still
//        // block)
//
//        while (depth <= ti.countStackFrames()) {
//            System.out.println("loop1: depth: " + depth + " countStackFrames: " + ti.countStackFrames());
//            Instruction nextPC = ti.executeInstructionHidden();
//
//            if (ti.getPendingException() != null) {
//
//            } else {
//                // ==================== EDITED
////              if (nextPC == pc) {
////                // BANG - we can't have CG's here
////                // should be rather an ordinary exception
////                // createAndThrowException("java.lang.AssertionError", "choice point in sync executed method: " + frame);
////                throw new JPFException("choice point in hidden method execution: " + frame);
////              } else {
////                pc = nextPC;
////              }
//                pc = nextPC;
//                // ==================== EDITED
//            }
//            System.out.println("loop2");
//        }
//
//        vm.getSystemState().decAtomic();
//
//        ti.nextPc = null;
//
//        System.out.println("exit executeMethodHidden");
//        // the frame was already removed by the RETURN insn of the frame's method
//    }

//    public static void startSecondJVM() throws Exception {
//        String separator = System.getProperty("file.separator");
//        String classpath = System.getProperty("java.class.path");
//        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
//        ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, Prueba.class.getName());
//        Process process = processBuilder.start();
//        process.waitFor();
//    }

//  System.out.println("\nStarting second JVM...\n");
//  // Trying to run another symbolic execution:
//  try {
//      startSecondJVM();
//  } catch (Exception e) {
//      System.out.println(e.getMessage());
//  }
//  System.out.println("\nSecond JVM Finished!!\n");
