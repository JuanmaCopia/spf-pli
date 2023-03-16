#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/treeset"
PACKAGE="heapsolving.treeset"


CLASS_NAME="TreeSet"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=50


METHOD="remove"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD REPOKSOLVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"


METHOD="add"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD REPOKSOLVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false"


# METHOD="contains"
# echo "-------- Method: $METHOD --------"


