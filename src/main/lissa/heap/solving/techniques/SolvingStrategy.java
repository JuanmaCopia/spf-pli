package lissa.heap.solving.techniques;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.config.ConfigParser;
import lissa.heap.testgen.args.Argument;
import lissa.heap.testgen.args.TargetMethod;
import symsolve.explorers.impl.SymmetryBreakStrategy;

public class SolvingStrategy {

    public int exploredPaths = 0;
    public int exceptionsThrown = 0;
    public static ConfigParser config;
    TargetMethod targetMethod;
    List<String> tests = new LinkedList<>();

    public static SolvingStrategy makeSymbolicHeapSolvingTechnique(ConfigParser config) {
        SolvingStrategy.config = config;
        switch (config.solvingStrategy) {
        case LISSA:
            return new LISSA();
        case LISSAM:
            return new LISSAM();
        case PLI:
            return new PLI();
        case PLIOPT:
            return new PLIOPT();
        case LISSANOSB:
            config.symmetryBreakingStrategy = SymmetryBreakStrategy.NO_SYMMETRY_BREAK;
            return new LISSA();
        case LIHYBRID:
            return new LIHYBRID();
        case DRIVER:
            return new SolvingStrategy();
        case IFREPOK:
            return new IFREPOK();
        case PLAINLAZY:
            return new PLAINLAZY();
        case REPOKSOLVER:
            return new REPOKSOLVER();
        default:
            throw new IllegalArgumentException("Invalid symbolic heap solving technique");
        }
    }

    public void pathFinished(ThreadInfo ti, Instruction current, Instruction next) {
        exploredPaths++;
        if (config.generateTests)
            generateTestCase(ti, current, next);
        if (config.checkPathValidity)
            checkPathValidity(ti, current, next);
    }

    void checkPathValidity(ThreadInfo ti, Instruction current, Instruction next) {
    }

    void generateTestCase(ThreadInfo ti, Instruction current, Instruction next) {
    }

    public void countException() {
        exceptionsThrown++;
    }

    public void setTargetMethod(String name, int numArgs) {
        targetMethod = new TargetMethod(config.symSolveSimpleClassName, name, numArgs);
    }

    public void setArgument(Argument arg, int index) {
        targetMethod.setArgument(arg, index);
    }

    public void setArgument(Argument arg) {
        targetMethod.setArgument(arg);
    }

    public TargetMethod getTargetMethod() {
        return targetMethod;
    }

    public List<String> getTests() {
        return tests;
    }

}
