# PLI: Precise Lazy Initialization

`PLI` is an efficient symbolic execution approach for programs that manipulate complex heap-allocated data structures with rich structural constraints.

## Getting Started

To build and run `spf-pli` you need Java 8 and Ant version 1.9

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

Create a .jpf dir in your home directory and create in it a file called "site.properties" with the following content, replacing `PATH-TO-INSTALLATION_FOLDER` with your installation folder path:
```
jpf-core=${user.home}/PATH-TO-INSTALLATION_FOLDER/jpf-core

jpf-symbc=${user.home}/PATH-TO-INSTALLATION_FOLDER/jpf-symbc

spf-pli=${user.home}/PATH-TO-INSTALLATION_FOLDER/spf-pli

extensions=${jpf-core},${jpf-symbc},${spf-pli}
```

### Compile projets:

```
cd jpf-core && ant build
cd ../jpf-symbc && ant build
cd ../spf-pli && ant build
```

## Run experiments:

Move to the spf-pli folder and run an experiment to check everything is working properly:
```
bash scripts/run_case_study.sh TreeMap remove 4 PLI
```