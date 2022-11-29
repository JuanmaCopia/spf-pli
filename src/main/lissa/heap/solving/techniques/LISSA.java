package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.canonicalizer.Canonicalizer;
import lissa.heap.solving.config.ConfigParser;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.vector.SymSolveVector;

public class LISSA extends SolvingStrategy {

	
    protected SymSolveHeapSolver heapSolver;
    protected Canonicalizer canonicalizer;


    public LISSA(ConfigParser config) {
    	System.out.println("\ncreating lissa");
    	this.config = config;
    	System.out.println("1");
        heapSolver = new SymSolveHeapSolver();
        System.out.println("2");
        canonicalizer = new Canonicalizer(heapSolver.getVectorFormat());
        System.out.println("3");
    }

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        return heapSolver.isSatisfiable(vector);
    }

    @Override
    public Integer getBoundForClass(String simpleClassName) {
    	HashMap<String, Integer> scopes = heapSolver.getDataScopes();
        return scopes.get(simpleClassName);
    }

	@Override
	public long getSolvingTime() {
		return heapSolver.getSolvingTime();
	}

}
