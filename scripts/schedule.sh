#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/schedule"
PACKAGE="heapsolving.schedule"


CLASS_NAME="Schedule"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=25


METHOD="quantumExpire"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLI $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD REPOKSOLVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


METHOD="addProcess"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLI $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD REPOKSOLVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


METHOD="finishAllProcesses"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLI $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD REPOKSOLVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


