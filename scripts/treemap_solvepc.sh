#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_upto.sh"

SRC_FOLDER="src/examples/heapsolving/treemap"
PACKAGE="heapsolving.treemap"


CLASS_NAME="TreeMap"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=50


METHOD="remove"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE

METHOD="put"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE

METHOD="containsKey"
echo "-------- Method: $METHOD --------"
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NTOPT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash $TARGET_SCRIPT $CLASS_NAME $METHOD NT $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE


# METHOD="containsValue"
# echo "-------- Method: $METHOD --------"
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSAM $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSANOSB $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE

# METHOD="get"
# echo "-------- Method: $METHOD --------"
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSAM $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
# bash $TARGET_SCRIPT $CLASS_NAME $METHOD LISSANOSB $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE