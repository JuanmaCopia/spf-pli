#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/hashmap"
PACKAGE="heapsolving.hashmap"


CLASS_NAME="HashMap"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=50


METHOD="remove"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLI $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


METHOD="put"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLI $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD PLIOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE "false" "false"


# METHOD="containsKey"
# echo "-------- Method: $METHOD --------"

# METHOD="containsValue"
# echo "-------- Method: $METHOD --------"

# METHOD="get"
# echo "-------- Method: $METHOD --------"