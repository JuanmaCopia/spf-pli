#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
#TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $SCRIPT_DIR/dictionaryinfo.sh 
bash $SCRIPT_DIR/hashmap.sh 
bash $SCRIPT_DIR/linkedlist.sh 
bash $SCRIPT_DIR/sqlfilterclauses.sh 