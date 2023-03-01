package lissa.listeners;

import java.util.LinkedList;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.config.ConfigParser;
import lissa.heap.SymHeapHelper;
import lissa.heap.solving.techniques.LIBasedStrategy;
import lissa.heap.solving.techniques.LIHYBRID;
import lissa.heap.solving.techniques.LISSAM;
import lissa.heap.solving.techniques.NT;
import lissa.heap.solving.techniques.NTOPT;
import lissa.heap.solving.techniques.PCCheckStrategy;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.utils.Utils;

public class HeapSolvingListener extends PropertyListenerAdapter {

    SolvingStrategy heapSolvingStrategy;
    ConfigParser config;

    long totalTime = 0;

    long solvingTime = 0;
    int cacheHits = 0;

    int exploredPaths = 0;
    int exceptionsThrown = 0;
    int invalidPaths = 0;
    int validPaths = 0;

    int prunedBranchesDueToPC = 0;
    long repOKPCSolvingTime = 0;

    public HeapSolvingListener(SolvingStrategy solvingStrategy, ConfigParser config) {
        this.heapSolvingStrategy = solvingStrategy;
        this.config = config;
    }

    @Override
    public void searchStarted(Search search) {
        if (!Utils.fileExist(config.resultsFileName)) {
            Utils.createFileAndFolders(config.resultsFileName, false);
            Utils.appendToFile(config.resultsFileName, getFileHeader());
        }
        this.totalTime = System.currentTimeMillis();
    }

//    @Override
//    public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
//        System.out.println("About to execute instruction: " + instructionToExecute + "   MI: "
//                + instructionToExecute.getMethodInfo());
//    }

    @Override
    public void instructionExecuted(VM vm, ThreadInfo ti, Instruction nextInsn, Instruction executedInsn) {
        SystemState ss = vm.getSystemState();

        if (executedInsn instanceof EXECUTENATIVE) { // break on method call
            EXECUTENATIVE exec = (EXECUTENATIVE) executedInsn;

            if (exec.getExecutedMethodName().equals("makeSymbolicImplicitInputThis")) {
                ChoiceGenerator<?> cg;

                if (!ti.isFirstStepInsn()) {
                    cg = new PCChoiceGenerator("initializePathCondition", 1);
                    ss.setNextChoiceGenerator(cg);
                    ti.reExecuteInstruction();
                } else {
                    PCChoiceGenerator curCg = (PCChoiceGenerator) ss.getChoiceGenerator();
                    curCg.getNextChoice();
                    curCg.setCurrentPC(new PathCondition());
                }
            } else if (exec.getExecutedMethodName().equals("pathFinished")) {
                heapSolvingStrategy.pathFinished(vm, vm.getCurrentThread());
                if (config.checkPathValidity)
                    ti.setNextPC(SymHeapHelper.createINVOKESTATICInstruction("checkPathValidity()V", executedInsn));

            } else if (exec.getExecutedMethodName().equals("exceptionThrown")) {
                heapSolvingStrategy.countException();
            }
        }
    }

    @Override
    public void searchFinished(Search search) {
        getStatistics();
        printReport();
        writeDataToFile(createStringData());
    }

    void getStatistics() {
        totalTime = (System.currentTimeMillis() - totalTime);
        exploredPaths = heapSolvingStrategy.exploredPaths;
        exceptionsThrown = heapSolvingStrategy.exceptionsThrown;

        if (heapSolvingStrategy instanceof LIBasedStrategy) {
            LIBasedStrategy stg = (LIBasedStrategy) heapSolvingStrategy;
            solvingTime = stg.getSolvingTime();

            if (config.checkPathValidity)
                validPaths = stg.validPaths;

            if (stg instanceof LISSAM) {
                cacheHits = ((LISSAM) stg).cacheHits;
            } else if (stg instanceof PCCheckStrategy) {
                prunedBranchesDueToPC = ((PCCheckStrategy) stg).getPrunedBranchCount();
                repOKPCSolvingTime = stg.getRepOKSolvingTime();
            }

        }
    }

    void printReport() {
        System.out.println("\n\nTechnique:  " + config.solvingStrategy.name());
        System.out.println(String.format("Method:     %s.%s", config.symSolveSimpleClassName, config.targetMethodName));
        System.out.println("Scope:      " + config.finitizationArgs);
        System.out.println("\n------- Statistics -------\n");
        System.out.println(" - Executed Paths:        " + exploredPaths);
        System.out.println(" - Exceptions thrown:        " + exceptionsThrown);
        if (heapSolvingStrategy instanceof LIHYBRID)
            System.out.println(" - Invalid Paths:         " + invalidPaths);
        System.out.println(" - Total Time:            " + totalTime / 1000 + " s.");
        if (heapSolvingStrategy instanceof LIBasedStrategy) {
            if (config.checkPathValidity)
                System.out.println(" - Valid Paths:           " + validPaths);
            System.out.println(" - Solving Time:          " + solvingTime / 1000 + " s.");
        }
        if (heapSolvingStrategy instanceof LISSAM)
            System.out.println(" - Cache Hits:            " + cacheHits);
        if (heapSolvingStrategy instanceof NT) {
            System.out.println(" - repOK PC solving time: " + repOKPCSolvingTime / 1000 + " s.");
            System.out.println(" - PC invalid branches  : " + prunedBranchesDueToPC);
            NT nt = (NT) heapSolvingStrategy;
            System.out.println(" - Total branches seen  : " + nt.primitiveBranches);
            if (heapSolvingStrategy instanceof NTOPT) {
                System.out.println(" - branches cache hits  : " + ((NTOPT) nt).primitiveBranchingCacheHits);
            }

        }
        System.out.println("");
    }

    String createStringData() {
        LinkedList<String> results = new LinkedList<String>();
        results.add(config.targetMethodName);
        results.add(config.solvingStrategy.name());
        results.add(config.finitizationArgs);
        results.add(Utils.calculateTimeInHHMMSS(totalTime));
        results.add(Utils.calculateTimeInHHMMSS(solvingTime));
        results.add(Utils.calculateTimeInHHMMSS(repOKPCSolvingTime));
        results.add(Integer.toString(exploredPaths));
        if (config.checkPathValidity)
            results.add(Integer.toString(validPaths));
        else
            results.add("-");
        results.add(Integer.toString(exceptionsThrown));
        String resultsData = results.toString();
        return resultsData.substring(1, resultsData.length() - 1);
    }

    void writeDataToFile(String data) {
        Utils.appendToFile(config.resultsFileName, data);
    }

    String getFileHeader() {
        return "Method,Technique,Scope,TotalTime,SymSolveTime,RepOKTime,TotalPaths,ValidPaths,ExceptionsThrown";
    }

}
