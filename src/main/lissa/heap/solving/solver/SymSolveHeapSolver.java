package lissa.heap.solving.solver;

import java.util.HashMap;

import korat.finitization.impl.CVElem;
import lissa.HeapSolvingInstructionFactory;
import lissa.heap.solving.config.ConfigParser;
import symsolve.SymSolve;
import symsolve.config.SolverConfig;
import symsolve.vector.SymSolveVector;


public class SymSolveHeapSolver {


    SymSolve solver;
    long solvingTime = 0;

    
    public SymSolveHeapSolver() {
        solver = createSymSolveInstance();
    }

    private SymSolve createSymSolveInstance() {
    	System.out.println("\ncreating symsolve instance");
    	ConfigParser conf = HeapSolvingInstructionFactory.getConfigParser();
    	System.out.println("1");
    	SolverConfig symSolveConfig = new SolverConfig(conf.symSolveClassName, conf.finitizationArgs, conf.symmetryBreakingStrategy, conf.predicateName);
    	System.out.println("22");
    	SymSolve solver = new SymSolve(symSolveConfig);
    	System.out.println("3");
        return solver;
    }

    public boolean isSatisfiable(SymSolveVector vector) {
    	long time = System.currentTimeMillis();
        boolean result = solver.isSatisfiable(vector);
        solvingTime += (System.currentTimeMillis() - time);
        return result;
    }

    public boolean isSatisfiableAutoHybridRepOK(SymSolveVector vector) {
        return solver.isSatAutoHybridRepOK(vector);
    }

    public HashMap<String, Integer> getDataScopes() {
        return solver.getScopes();
    }

    public CVElem[] getVectorFormat() {
        return solver.getVectorFormat();
    }

    public long getSolvingTime() {
    	return solvingTime;
    }

}
