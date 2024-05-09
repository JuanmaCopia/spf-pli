#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/transportstats"
PACKAGE="heapsolving.transportstats"

CLASS_NAME="TransportStats"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=25


METHOD="bytesRead"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"



METHOD="bytesWritten"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"

