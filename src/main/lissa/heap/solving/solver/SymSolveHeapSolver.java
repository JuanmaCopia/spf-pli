package lissa.heap.solving.solver;

import java.util.HashMap;

import korat.finitization.impl.CVElem;
import lissa.heap.HeapSolvingInstructionFactory;
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
    	ConfigParser conf = HeapSolvingInstructionFactory.getConfigParser();
    	SolverConfig symSolveConfig = new SolverConfig(conf.symSolveClassName, conf.finitizationArgs, conf.symmetryBreakingStrategy, conf.predicateName);
    	SymSolve solver = new SymSolve(symSolveConfig);
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
