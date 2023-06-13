package pli.bytecode.lazy;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
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
import pli.LISSAShell;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.solving.techniques.LIBasedStrategy;
import pli.heap.solving.techniques.PLAINLAZY;

public class GETFIELDHeapSolving extends gov.nasa.jpf.jvm.bytecode.GETFIELD {
    public GETFIELDHeapSolving(String fieldName, String clsName, String fieldDescriptor) {
        super(fieldName, clsName, fieldDescriptor);
    }

    // private int numNewRefs = 0; // # of new reference objects to account for
    // polymorphism -- work of Neha Rungta -- needs to be updated

    boolean abstractClass = false;

    @Override
    public Instruction execute(ThreadInfo ti) {
        LIBasedStrategy heapSolvingStrategy = (LIBasedStrategy) LISSAShell.solvingStrategy;

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

        Object attr = ei.getFieldAttr(fi);
        // check if the field is of ref type & it is symbolic (i.e. it has an attribute)
        // if it is we need to do lazy initialization

        ClassInfo typeClassInfo = fi.getTypeClassInfo(); // use this instead of fullType
        String fullClassName = typeClassInfo.getName();

        if (heapSolvingStrategy instanceof PLAINLAZY) {
            PLAINLAZY lazySTG = (PLAINLAZY) heapSolvingStrategy;
            if (lazySTG.isInfiniteLoopHeuristic(fi, attr, objRef)) {
                ti.getVM().getSystemState().setIgnored(true); // Backtrack
                return this;
            }
        }

        String ownerClassName = ei.getClassInfo().getName();
        String fieldName = fi.getName();
        if (!heapSolvingStrategy.isFieldTracked(ownerClassName, fieldName)) {
            return super.execute(ti);
        }

        if (!fi.isReference() || attr == null || attr instanceof StringExpression
                || attr instanceof SymbolicStringBuilder || !heapSolvingStrategy.isClassInBounds(fullClassName)

        ) {
            return super.execute(ti);
        }

        if (heapSolvingStrategy instanceof PLAINLAZY)
            ((PLAINLAZY) heapSolvingStrategy).resetGetFieldCount();

        // Lazy initialization:

        int currentChoice;
        ChoiceGenerator<?> thisHeapCG;

        if (!ti.isFirstStepInsn()) {
            prevSymRefs = null;
            numSymRefs = 0;

            prevHeapCG = ti.getVM().getSystemState().getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
            // to check if this still works in the case of cascaded choices...

            if (prevHeapCG != null) {
                // determine # of candidates for lazy initialization
                SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) ((HeapChoiceGeneratorLISSA) prevHeapCG)
                        .getCurrentSymInputHeap();
                prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
                numSymRefs = prevSymRefs.length;
            }
            int increment = 2;
            if (typeClassInfo.isAbstract()) {
                abstractClass = true;
                increment = 1; // only null
            }

            thisHeapCG = new HeapChoiceGeneratorLISSA("lazyInit", numSymRefs + increment); // +null,new
            ti.getVM().getSystemState().setNextChoiceGenerator(thisHeapCG);
            // ti.reExecuteInstruction();
            if (SymbolicInstructionFactory.debugMode)
                System.out.println("# heap cg registered: " + thisHeapCG);
            return this;

        } else { // this is what really returns results
            // here we can have 2 choice generators: thread and heappc at the same time?

            frame.pop(); // Ok, now we can remove the object ref from the stack

            thisHeapCG = ti.getVM().getSystemState().getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
            assert (thisHeapCG != null
                    && thisHeapCG instanceof HeapChoiceGeneratorLISSA) : "expected HeapChoiceGeneratorLISSA, got: "
                            + thisHeapCG;
            currentChoice = ((HeapChoiceGeneratorLISSA) thisHeapCG).getNextChoice();
        }

        PathCondition pcHeap; // this pc contains only the constraints on the heap
        SymbolicInputHeapLISSA symInputHeap;

        // depending on the currentChoice, we set the current field to an object that
        // was already created
        // 0 .. numymRefs -1, or to null or to a new object of the respective type,
        // where we set all its
        // fields to be symbolic

        prevHeapCG = thisHeapCG.getPreviousChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);

        if (prevHeapCG == null) {
            pcHeap = new PathCondition();
            symInputHeap = new SymbolicInputHeapLISSA();
        } else {
            pcHeap = ((HeapChoiceGeneratorLISSA) prevHeapCG).getCurrentPCheap();
            symInputHeap = (SymbolicInputHeapLISSA) ((HeapChoiceGeneratorLISSA) prevHeapCG).getCurrentSymInputHeap();
        }

        assert pcHeap != null;
        assert symInputHeap != null;

        prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
        numSymRefs = prevSymRefs.length;

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        int daIndex = 0; // index into JPF's dynamic area
        if (currentChoice < numSymRefs) { // lazy initialization using a previously lazily initialized object
            HeapNode candidateNode = prevSymRefs[currentChoice];
            pcHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, candidateNode.getSymbolic());
            daIndex = candidateNode.getIndex();
            symRefInput.addReferenceField(objRef, fi, daIndex);

        } else if (currentChoice == numSymRefs) { // null object
            pcHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, new IntegerConstant(-1));
            daIndex = MJIEnv.NULL;
            symRefInput.addReferenceField(objRef, fi, SymbolicReferenceInput.NULL);

        } else if (currentChoice == (numSymRefs + 1) && !abstractClass) {
            // creates a new object with all fields symbolic and adds the object to
            // SymbolicHeap

            Integer bound = heapSolvingStrategy.getBoundForClass(fullClassName);
            // backtrack if the max bound of nodes has been reached
            if (numSymRefs == bound) {
                ti.getVM().getSystemState().setIgnored(true);
                return this;
            }

            daIndex = SymHeapHelper.addNewHeapNode(typeClassInfo, ti, attr, pcHeap, symInputHeap, numSymRefs,
                    prevSymRefs, ei.isShared());

            symRefInput.addReferenceField(objRef, fi, daIndex);
        } else {
            System.err.println("subtyping not handled");
        }

        ei.setReferenceField(fi, daIndex);
        ei.setFieldAttr(fi, null);

        frame.pushRef(daIndex);

        HeapChoiceGeneratorLISSA heapCG = (HeapChoiceGeneratorLISSA) thisHeapCG;
        heapCG.setCurrentPCheap(pcHeap);
        heapCG.setCurrentSymInputHeap(symInputHeap);
        if (SymbolicInstructionFactory.debugMode)
            System.out.println("GETFIELD pcHeap: " + pcHeap);

        if (!heapSolvingStrategy.isRepOKExecutionMode()) {
            return heapSolvingStrategy.handleLazyInitializationStep(ti, this, getNext(ti), heapCG);
        }
        return getNext(ti);
    }
}
