#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
#TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $SCRIPT_DIR/avltree.sh 
bash $SCRIPT_DIR/binomialheap.sh 
bash $SCRIPT_DIR/schedule.sh 
bash $SCRIPT_DIR/combatantstatistic.sh 
