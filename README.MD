# Artifact of Precise Lazy Initialization for Programs with Complex Heap Inputs

## Artifact Description
Lazy initialization enables symbolic execution for programs with heap-allocated inputs. In this artifact we present a prototype implementation of our novel PLI (Precise Lazy Initialization) approach for the symbolic execution of such programs. The approach precisely decides whether there exists a concretization of the current symbolic heap that satisfies the program's precondition. Unlike previous approaches, PLI also takes into account the constraints in the path condition to determine the feasibility of the current symbolic heap. Furthermore, PLI allows preconditions to be specified as standard operational predicates for concrete structures, eliminating the need for additional specifications tailored to symbolic heaps. The artifact provides the source code of PLI, and detailed documentation with instructions for: installing the tool, reproducing all the experiments in the paper, and running the tool in new, user defined case studies. Thus, we apply for the Available, Reviewed and Reproducible badges for our artifact.

### Folder Structure

- The source code of PLI can be found in `src/main/pli`
- The source code of case studies can be found in `src/examples/heapsolving`
- The execution metrics of will be saved in `output/results`.


## Environment Setup

We recommend [installing `PLI` using docker.](#install-using-docker).

### Install Using Docker

Clone this repository and move inside the cloned folder:
```
git clone https://github.com/JuanmaCopia/spf-pli/tree/artifact && cd spf-pli
```

### Recommended setup: Linux, Windows and Mac OS X on Intel sillicon

Build the docker container:
```
docker build -t pli .
```
Run the container:
```
docker run -it pli:latest /bin/bash
```

**Note:** The experiments in the paper were run in Linux using an Intel CPU.

### Setup for Mac OS X on Apple sillicon

**Warning:** For `Symbolic Pathfinder` with the `Z3` solver to run in Apple sillicon (and therefore for running `PLI`), a Docker container for the amd64 platform must be created (an arm64 container did not work for me). Thus, the container must run in emulated mode in the Apple CPU, which might produce a significant performance hit and some issues (see below).

Build the docker container using `buildx`[0]:
```
docker buildx create --name amd64builder
docker buildx use amd64builder
docker buildx build --platform linux/amd64 -t pli . --load
```

**Warning:** The last command might hang a few times. I cancelled the command when it was taking too long and restarted it. After repeating this process a few times (about 3) I got a working installation.

Run the container:
```
docker run --platform linux/amd64 -it pli:latest /bin/bash
```

[0] https://docs.docker.com/desktop/multi-arch/



## Getting Started

Move inside the `spf-pli` folder and run an experiment to check everything is working properly:
```
bash scripts/run_case_study.sh TreeMap remove 4 PLI
```

The script `run_case_study.sh` runs a specific subject with the specified technique and scope:
```
bash scripts/run_case_study.sh <SUBJECT> <METHOD> <SCOPE> <TECHNIQUE>
```

The subject programs are:
```
CLASS               TARGET METHODS

TreeMap:               put, remove
TreeSet:               add, remove
HashMap:               put, remove
LinkedList:            add, remove
Schedule:              addProcess, quantumExpire, finishAllProcesses
AvlTree:               insert, remove
BinomialHeap           insert, extractMinFixed, extractMinBugged
TransportStats         bytesRead, bytesWritten
CombatantStatistic     addData, ensureTypExists
SQLFilterClauses       get, put
Template               addParameter, getParameter
DictionaryInfo         addField, getField
```

The techniques are:
```
PLIOPT       Our proposed technique with the optimization enabled
PLI:         Our proposed technique with the optimization disabled
LISSA
IFREPOK
DRIVER
LIHYBRID
```

## Reproducibility Instructions

To reproduce all the experiments for a specific case study we provide the following scripts:
```
bash scripts/treemap.sh
bash scripts/hashmap.sh
bash scripts/treeset.sh
bash scripts/linkedlist.sh
bash scripts/schedule.sh
bash scripts/transportstats.sh
bash scripts/dictionaryinfo.sh
bash scripts/combatantstatistic.sh
bash scripts/template.sh
bash scripts/sqlfilterclauses.sh
bash scripts/binomialheap.sh
bash scripts/avltree.sh
```

For example, one also have the possibility to run only `TreeMap` experiments by running:
```
bash scripts/treemap.sh
```

The above script will run all the techniques over the `TreeMap` methods `insert` and `remove` for increasingly large scopes, starting from 1. Each scope is automatically stopped if it exceeds the 1-hour timeout.

The above scripts produce the data from where the graphics of `RQ1` and `RQ2` and the table of `RQ3` of the paper were generated.

**Important:** This might take several days to finish!

### Understanding the output of the analysis

All the outputs of the technique will be placed inside the `spf-pli/output/` folder.

For example, the metrics of each run are stored under `spf-pli/output/results/<CLASS_NAME>-results.csv`, where `<CLASS_NAME>` is the name of the class containing the method under analysis.
Each row in the `.csv` file is an individual run. The columns can be interpreted as follows:

- `Method`: The name of the method under analysis (e.g. remove).
- `Technique`: The approach employed to deal with the heaps (e.g. PLIOPT).
- `Scope`: The scope used (e.g. 4).
- `TotalTime`: Total time of the analysis.
- `SymSolveTime`: The time spent solving `preH` with SymSolve.
- `RepOKTime`: The time spent executing the symbolic execution of `preP`.
- `TotalPaths`: Total number of explored paths.
- `ValidPaths`: Number of valid paths (deactivated by default, can reduce performance).
- `ExceptionsThrown`: The number of paths that threw exceptions.
- `SolvingCalls`: The number of times the PLI's solving algorithm was called.


### Folder structure of a case study

The source files of the case studies can be found in: `spf-pli/src/examples/heapsolving`. Each case study have the following files and folders:

```
├── <ClassName>.java             --> The class containing the methods under test, repOKs and the finitization method (scopes for the analysis).
├── <ClassName>.jpf              --> The configuration file
├── <ClassName>Harness.java      --> A Harness necessary to run techniques that require perform previous actions before SUT's execution (IFREPOK and DRIVER).
├── <method 1>                   --> A folder for each of the SUTs, containing the main entry for the SUT
│   └── <ClassName>Main.java     --> A java class that contains the main entry that calls SUT
├── <method 2>
│   └── <ClassName>Main.java
├── <method 3>
│   └── <ClassName>Main.java
```

### Preconditions (repOK Methods)

In the case studies, `preH` precondition is called `repOKSymSolve` and `preP` precondition is called `repOKSymbolicExecution`.

### Defining the scopes of the analysis

Finitization methods specify the scopes for the analysis. PLI requires the finitization method have the following signature:
```
public static IFinitization fin<CLASS_NAME>(<FINITIZATION_ARGUMENTS>)
```
Where the `<CLASS_NAME>` is the class containing the method under analysis.

Check for example the finitization of the `TreeMap` case study:
```
 public static IFinitization finTreeMap(int nodesNum) {
    IFinitization f = FinitizationFactory.create(TreeMap.class);
    IObjSet nodes = f.createObjSet(Entry.class, nodesNum, true);
    f.set(TreeMap.class, "root", nodes);
    f.set(TreeMap.class, "size", f.createIntSet(0, nodesNum));
    f.set(Entry.class, "key", f.createIntSet(0, nodesNum - 1));
    f.set(Entry.class, "left", nodes);
    f.set(Entry.class, "right", nodes);
    f.set(Entry.class, "parent", nodes);
    f.set(Entry.class, "color", f.createBooleanSet());
    return f;
}
```

- The method `FinitizationFactory.create(<CLASS>)` creates a finitization for the class.
- `createObjSet(<CLASS>, <NUMBER_OF_OBJECTS>, <ALLOWS_NULL>)` method from the finitization creates a set of `<NUMBER_OF_OBJECTS>` objects of type `<CLASS>` that can be used as field domain for the fields of the structure.
- `createIntSet(<FIRST>, <LAST>)` method from the finitization creates a range of integers from `<FIRST>` to `<LAST>`
- The method `set(<CLASS>, <FIELD_NAME>, <SET_OF_VALUES>)` established the `<SET_OF_VALUES>` as the field domain of the field `<FIELD_NAME>` that belongs to `<CLASS>`.


### Required hard-coded methods

To run the technique `PLI` and `PLIOPT` is necessary that the class containing the method under analysis also contains the following hard-coded static method called `runRepOK`:
```
public static void runRepOK() {
    <CLASS_NAME> toBuild = new <CLASS_CONSTRUCTOR>();
    SymHeap.buildSolutionHeap(toBuild);
    SymHeap.handleRepOKResult(toBuild, toBuild.repOKSymbolicExecution());
}
```
Where the `<CLASS_NAME>` is the class containing the method under analysis (e.g. TreeMap) and `<CLASS_CONSTRUCTOR>` is the constructor method.
This method is used to trigger and handle the result of the symbolic execution of `preP` (`repOKSymbolicExecution`).

Optionally, to use the features `heapsolving.checkPathValidity` and `heapsolving.generateTests` (still under development), the following static method called `runRepOKComplete`
```
public static void runRepOKComplete() {
    <CLASS_NAME> toBuild = new <CLASS_CONSTRUCTOR>();
    SymHeap.buildPartialHeapInput(toBuild);
    SymHeap.handleRepOKResult(toBuild, toBuild.repOKComplete());
}
```

### Parameters in Configuration File

The script run_case_study.sh modifies the configuration files (`.jpf`) of the case studies. To run other programs is required to use a custom configuration file.


Configuration file template:
```
classpath=${spf-pli}/build/examples
sourcepath=${spf-pli}/src/examples

@using=spf-pli

# ---------------   Arguments   ---------------

shell=pli.LISSAShell

target = <CLASS_CONTAINING_MAIN>
method = <METHOD_NAME>

symbolic.debug = false
symbolic.dp = z3

heapsolving.strategy = <HEAP_SOLVING_STRATEGY>
heapsolving.checkPathValidity = <BOOLEAN>
heapsolving.generateTests = <BOOLEAN>

heapsolving.symsolve.finitization.class = <CLASS_CONTAINING_REPOK_AND_FINITIZATION>
heapsolving.symsolve.finitization.args = <MAX_SCOPE>
heapsolving.symsolve.predicate = <PRE_H_NAME>

jvm.insn_factory.class=pli.HeapSolvingInstructionFactory
vm.insn_factory.class=pli.HeapSolvingInstructionFactory

# ---------------------------------------------
```

Explanation of parameters:

- `target`: The class that contains the main method.
- `method`: The name of the program under analysis (just for output purposes)
- `heapsolving.strategy`: The approach to be used to deal with the heap constraints.
- `heapsolving.checkPathValidity`: check the validity of paths using PLI (when set to true the performance can decrease).
- `heapsolving.generateTests`: generate tests using direct heap manipulation (when set to true the performance can decrease).
- `heapsolving.symsolve.finitization.class`: The class containing the finitization and the repOKs methods
- `heapsolving.symsolve.finitization.args`: Comma separated arguments for the finitization method (scope).
- `heapsolving.symsolve.predicate`: Name of the predicate to be used by `SymSolve` (preH). It must be inside the previously indicated class.


For example, to run the `remove` method from `TreeMap` for a scope of `4` using the `PLIOPT` technique:
```
classpath=${spf-pli}/build/examples
sourcepath=${spf-pli}/src/examples

@using=spf-pli

# ---------------   Arguments   ---------------

shell=pli.LISSAShell

target = heapsolving.treemap.remove.TreeMapMain
method = remove

symbolic.debug = false
symbolic.dp = z3

heapsolving.strategy = PLIOPT
heapsolving.checkPathValidity = false
heapsolving.generateTests = false

heapsolving.symsolve.finitization.class = heapsolving.treemap.TreeMap
heapsolving.symsolve.finitization.args = 4
heapsolving.symsolve.predicate = repOKSymSolve

jvm.insn_factory.class=pli.HeapSolvingInstructionFactory
vm.insn_factory.class=pli.HeapSolvingInstructionFactory

# ---------------------------------------------
```

## Running PLI on a new case study

In the following steps we explain how to run `PLI` on a new case study.

Let's assume that we want to analyze a method inside the class `TreeMap`. There is already a folder prepared with the class `TreeMap` inside the folder `src/examples/mycasestudy/`


### Step 1: Write the specifications preH and PreP

The first step is write both specifications. `preH` must the heap-related constraints and `preP` the primitive-typed constraints. The specifications can receive any name, but later the name should be specified in the configuration. Fortunately, the class `TreeMap` already have included the specifications.


### Step 2: Write the Finitization method

The next step requires to write the finitization method that defines the scopes for the analysis. In [Defining the scopes of the analysis](#defining-the-scopes-of-the-analysis),we explain how to write finitization methods. For this case, the finitization for `TreeMap` is already defined in the class.


### Step 3: Add the required hard-coded method

In [Required hard-coded methods](#required-hard-coded-methods), we explain how to write the required hard-coded method by PLI. This is also already included in the file.


### Step 4: Create the Main method that calls the method under analysis

In the folder you will also find a `MyMain.java` file, with the following content.

```
package mycasestudy;

import pli.SymHeap;

public class MyMain {

    public static void main(String[] args) {
        TreeMap structure = new TreeMap();
        structure = (TreeMap) SymHeap.makeSymbolicRefThis("treemap_0", structure);
        int key = SymHeap.makeSymbolicInteger("INPUT_KEY");

        structure.containsKey(key);  // Call to method under analysis

        SymHeap.pathFinished();
    }
}
```
The main creates the symbolic inputs and then calls the method under analysis, in this case `containsKey`.

- The method `SymHeap.makeSymbolicRefThis` makes an instance of reference type symbolic.
- The method `SymHeap.makeSymbolicInteger` creates a symbolic integer.

### Step 5: Create the configuration file

The configuration file called `config.jpf` can also be found inside the folder `src/examples/mycasestudy/`, and contains the following content.

```
@using=spf-pli

# ---------------   Arguments   ---------------

shell=pli.LISSAShell

target = mycasestudy.MyMain
method = METHODNAME

symbolic.debug = false
symbolic.dp = z3

heapsolving.strategy = PLIOPT
heapsolving.checkPathValidity = false
heapsolving.generateTests = false
heapsolving.symsolve.finitization.class = mycasestudy.TreeMap
heapsolving.symsolve.finitization.args = 6
heapsolving.symsolve.predicate = repOKSymSolve

jvm.insn_factory.class=pli.HeapSolvingInstructionFactory
vm.insn_factory.class=pli.HeapSolvingInstructionFactory

# ---------------------------------------------
```
- Notice that in `target` we specified the name of the class containing the `main` method.
- In `heapsolving.strategy` the `PLIOPT` technique is specified. You can also try just `PLI`.
- In `heapsolving.symsolve.finitization.class` we specified the class containing the finitization `mycasestudy.TreeMap`
- In `heapsolving.symsolve.finitization.args` we specified a scope of 6 nodes
- In `heapsolving.symsolve.predicate` we specified the name of preH to be used by SymSolve `repOKSymSolve`

For more information about parameters, refer to: [Parameters in Configuration File](#parameters-in-configuration-file)


### Step 6: Compile the code and run PLI

Compile the code with:
```
ant build
```

After the build finishes you can run the newly added study case with:
```
bash scripts/run.sh src/examples/mycasestudy/config.jpf
```

Below is an excerpt with the relevant parts of `PLI`'s output:
```
====================================================== system under test
mycasestudy.MyMain.main()

====================================================== search started: 8/21/23 10:35 PM


Technique:  PLIOPT
Method:     TreeMap.METHODNAME
Scope:      6

------- Statistics -------

 - Executed Paths:        46
 - Exceptions thrown:     0
 - Total Time:            0 s.
 - Solving Time:          0 s.
 - repOK PC solving time: 0 s.
 - Solving procedure calls:   130


====================================================== results
no errors detected

```
