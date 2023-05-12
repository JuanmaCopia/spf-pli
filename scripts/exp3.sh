#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
#TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $SCRIPT_DIR/template.sh 
bash $SCRIPT_DIR/transportstats.sh 
bash $SCRIPT_DIR/treemap.sh 
bash $SCRIPT_DIR/treeset.sh 