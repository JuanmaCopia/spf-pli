package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.MJIEnv;
import lissa.heap.builder.HeapSolutionBuilder;

public class LISSAPC extends LISSA {

    HeapSolutionBuilder builder;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace());
    }

    public void buildSolutionHeap(MJIEnv env, int objRef) {
//        System.out.println("\n\n LISSAPC: BUILD SOLUTION \n");
        builder.buildSolution(env, objRef, currentSymbolicInput, heapSolver.getCurrentSolutionVector());
    }
}
