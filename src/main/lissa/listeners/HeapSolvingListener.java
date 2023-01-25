package lissa.listeners;

import java.util.LinkedList;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import lissa.config.ConfigParser;
import lissa.heap.solving.techniques.LIBasedStrategy;
import lissa.heap.solving.techniques.LIHYBRID;
import lissa.heap.solving.techniques.LISSAM;
import lissa.heap.solving.techniques.LISSAPC;
import lissa.heap.solving.techniques.PCCheckStrategy;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.utils.Utils;

public class HeapSolvingListener extends PropertyListenerAdapter {

    SolvingStrategy heapSolvingStrategy;
    ConfigParser configParser;

    long totalTime = 0;

    long solvingTime = 0;
    int cacheHits = 0;

    int exploredPaths = 0;
    int invalidPaths = 0;

    int prunedBranchesDueToPC = 0;
    long repOKPCSolvingTime = 0;

    public HeapSolvingListener(SolvingStrategy solvingStrategy, ConfigParser configParser) {
        this.heapSolvingStrategy = solvingStrategy;
        this.configParser = configParser;
    }

    @Override
    public void searchStarted(Search search) {
        if (!Utils.fileExist(configParser.resultsFileName)) {
            Utils.createFileAndFolders(configParser.resultsFileName, false);
            Utils.appendToFile(configParser.resultsFileName, getFileHeader());
        }
        this.totalTime = System.currentTimeMillis();
    }

//    @Override
//    public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
//        System.out.println("About to execute instruction: " + instructionToExecute + "   MI: "
//                + instructionToExecute.getMethodInfo());
//    }

    @Override
    public void searchFinished(Search search) {
        getStatistics();
        printReport();
        writeDataToFile(createStringData());
    }

    void getStatistics() {
        totalTime = (System.currentTimeMillis() - totalTime);
        exploredPaths = heapSolvingStrategy.exploredPaths;

        if (heapSolvingStrategy instanceof LIBasedStrategy) {
            solvingTime = ((LIBasedStrategy) heapSolvingStrategy).getSolvingTime();

            if (heapSolvingStrategy instanceof LIHYBRID) {
                invalidPaths = ((LIHYBRID) heapSolvingStrategy).invalidPaths;
            } else if (heapSolvingStrategy instanceof LISSAM) {
                cacheHits = ((LISSAM) heapSolvingStrategy).cacheHits;
            } else if (heapSolvingStrategy instanceof PCCheckStrategy) {
                prunedBranchesDueToPC = ((PCCheckStrategy) heapSolvingStrategy).getPrunedBranchCount();
                repOKPCSolvingTime = ((PCCheckStrategy) heapSolvingStrategy).getRepOKSolvingTime();
            }

        }
    }

    void printReport() {
        System.out.println("\n\nTechnique:  " + configParser.solvingStrategy.name());
        System.out.println(String.format("Method:     %s.%s", configParser.symSolveSimpleClassName,
                configParser.targetMethodName));
        System.out.println("Scope:      " + configParser.finitizationArgs);
        System.out.println("\n------- Statistics -------\n");
        System.out.println(" - Executed Paths:        " + exploredPaths);
        if (heapSolvingStrategy instanceof LIHYBRID)
            System.out.println(" - Invalid Paths:         " + invalidPaths);
        System.out.println(" - Total Time:            " + totalTime / 1000 + " s.");
        if (heapSolvingStrategy instanceof LIBasedStrategy)
            System.out.println(" - Solving Time:          " + solvingTime / 1000 + " s.");
        if (heapSolvingStrategy instanceof LISSAM)
            System.out.println(" - Cache Hits:            " + cacheHits);
        if (heapSolvingStrategy instanceof LISSAPC) {
            System.out.println(" - repOK PC solving time: " + repOKPCSolvingTime / 1000 + " s.");
            System.out.println(" - PC invalid branches  : " + prunedBranchesDueToPC);
            LISSAPC lissaPC = (LISSAPC) heapSolvingStrategy;
            System.out.println(" - Total branches seen  : " + lissaPC.primitiveBranches);
            System.out.println(" - branches cache hits  : " + lissaPC.primitiveBranchingCacheHits);

        }
        System.out.println("");
    }

    String createStringData() {
        LinkedList<String> results = new LinkedList<String>();
        results.add(configParser.targetMethodName);
        results.add(configParser.solvingStrategy.name());
        results.add(configParser.finitizationArgs);
        results.add(Utils.calculateTimeInHHMMSS(totalTime));
        results.add(Utils.calculateTimeInHHMMSS(solvingTime));
        results.add(Utils.calculateTimeInHHMMSS(repOKPCSolvingTime));
        results.add(Integer.toString(exploredPaths));
        String resultsData = results.toString();
        return resultsData.substring(1, resultsData.length() - 1);
    }

    void writeDataToFile(String data) {
        Utils.appendToFile(configParser.resultsFileName, data);
    }

    String getFileHeader() {
        return "Method,Technique,Scope,TotalTime,SymSolveTime,RepOKTime,ExecutedPaths";
    }

}
