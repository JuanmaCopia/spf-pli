#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/avltree"
PACKAGE="heapsolving.avltree"


CLASS_NAME="AvlTree"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=50


METHOD="insert"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE


METHOD="remove"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE


