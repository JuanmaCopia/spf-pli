#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/binomialheap"
PACKAGE="heapsolving.binomialheap"


CLASS_NAME="BinomialHeap"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=25


METHOD="extractMinFixed"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


METHOD="insert"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


METHOD="extractMinBugged"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"






