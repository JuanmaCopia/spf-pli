package lissa.heap.solving.techniques;

import lissa.heap.builder.HeapSolutionBuilder;

public class LISSAPC extends LISSA {

    HeapSolutionBuilder builder;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace());
    }

    public void buildSolutionHeap(int objRef) {
        System.out.println("\n\n LISSAPC: BUILD SOLUTION \n");
        builder.buildSolution(heapSolver.getCurrentSolutionVector(), objRef);
//      ElementInfo eiRef = VM.getVM().getHeap().getModifiable(objvRef);

    }
}
