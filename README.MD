# PLI: Precise Lazy Initialization

`PLI` is an efficient symbolic execution approach for programs that manipulate complex heap-allocated data structures with rich structural constraints.

## Requirements

To build and run `spf-pli` you need Java 8 and Ant version 1.9

If you are using Mac or Windows we recommend [installing `PLI` using docker.](#install-using-docker)

## Install Without Docker

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

Build the docker container:
```
docker build -t pli .
```
Run the container:
```
docker run -it pli:latest /bin/bash
```

## Run experiments:

Inside the `spf-pli` folder and run an experiment to check everything is working properly:
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

**Important:** This might take several days to finish!

## Understanding the output of the analysis

The metrics are stored under `spf-pli/output/results/<CLASS_NAME>-results.csv`, where `<CLASS_NAME>` is the name of the class containing the method under analysis.

