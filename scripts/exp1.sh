#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

bash "$SCRIPT_DIR/treemap.sh"
bash "$SCRIPT_DIR/binomialheap.sh"
bash "$SCRIPT_DIR/schedule.sh"
bash "$SCRIPT_DIR/hashmap.sh"
bash "$SCRIPT_DIR/linkedlist.sh"
bash "$SCRIPT_DIR/treeset.sh"