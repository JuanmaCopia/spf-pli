#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/combatantstatistic"
PACKAGE="heapsolving.combatantstatistic"


CLASS_NAME="CombatantStatistic"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=25


METHOD="addData"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"



METHOD="ensureTypExists"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD X $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"

