#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"



TARGET_SCRIPT="${SCRIPT_DIR}/treemap.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/binomialheap.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/hashmap.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/avltree.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"


TARGET_SCRIPT="${SCRIPT_DIR}/linkedlist.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/treeset.sh"
bash $TARGET_SCRIPT
