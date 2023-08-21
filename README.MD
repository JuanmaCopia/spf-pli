# PLI: Precise Lazy Initialization

`PLI` is an efficient symbolic execution approach for programs that manipulate complex heap-allocated data structures with rich structural constraints.

We recommend [installing `PLI` using docker.](#install-using-docker) `PLI` can also be compiled from the source code following the instructions below.

## Install From Source Code

To build and run `spf-pli` you need Java 8 and Ant version 1.9.

First, let's install the required `jpf-core` and the `jpf-symbc` projects.

Create and move to the folder where you want to install the tool:
```
mkdir INSTALLATION_FOLDER && cd INSTALLATION_FOLDER
```

Clone the Java Pathfinder (`jpf-core`):
```
git clone https://github.com/JuanmaCopia/jpf-core
```

Clone the Symbolic Java Pathfinder extension (`jpf-symbc`):
```
git clone https://github.com/JuanmaCopia/jpf-symbc
```

Clone the `PLI` extension:
```
git clone https://github.com/JuanmaCopia/spf-pli
```

### Configuration

Create a .jpf directory inside your home directory and create in it a file called "site.properties":
```
cd ~
mkdir .jpf && cd .jpf
cat > site.properties
```

Paste the following text inside the new site.properties file, replacing `<PATH-TO-INSTALLATION_FOLDER>` with the location of each of the previously cloned repos:
```
jpf-core=${user.home}/<PATH-TO-INSTALLATION_FOLDER>/jpf-core

jpf-symbc=${user.home}/<PATH-TO-INSTALLATION_FOLDER>/jpf-symbc

spf-pli=${user.home}/<PATH-TO-INSTALLATION_FOLDER>/spf-pli

extensions=${jpf-core},${jpf-symbc}
```

### Compile projects:

Move to the installation folder:
```
cd <PATH-TO-INSTALLATION_FOLDER>
```

Compile each project:
```
cd jpf-core && ant build
cd ../jpf-symbc && ant build
cd ../spf-pli && ant build
```

## Install Using Docker

Clone this repository and move inside the cloned folder:
```
git clone https://github.com/JuanmaCopia/spf-pli && cd spf-pli
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

## Run experiments:

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

## Running all the experiments

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

## Understanding the output of the analysis

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


## Folder structure of a case study

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

## The Case Study Source Code: Preconditions and Analysis Scopes

### Preconditions (repOK Methods)

In the case studies, `preH` precondition is called `repOKSymSolve` and `preP` precondition is called `repOKSymbolicExecution`.

### Scopes (Finitization Methods)

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

The method `FinitizationFactory.create(<CLASS>)` creates a finitization for the class.
`createObjSet(<CLASS>, <NUMBER_OF_OBJECTS>, <ALLOWS_NULL>)` method from the finitization creates a set of `<NUMBER_OF_OBJECTS>` objects of type `<CLASS>` that can be used as field domain for the fields of the structure.
`createIntSet(<FIRST>, <LAST>)` method from the finitization creates a range of integers from `<FIRST>` to `<LAST>`
The method `set(<CLASS>, <FIELD_NAME>, <SET_OF_VALUES>)` established the `<SET_OF_VALUES>` as the field domain of the field `<FIELD_NAME>` that belongs to `<CLASS>`.


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

## Parameters in Configuration File

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

## Run configurations for Eclipse

The repo also includes the run configurations for the Eclipse IDE.

To run `PLI` in eclipse is necessary to:

1. Import the three repositories (jpf-core, jpf-symbc and spf-pli).
2. Set the parameters in the configuration file `.jpf`.
3. With the configuration file opened and focused on eclipse, execute the run configuration called `"run-spf-pli"`. The run configuration takes as input the configuration file that is focused on the IDE.