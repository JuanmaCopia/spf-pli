#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $TARGET_SCRIPT TreeMap remove 11 PLIOPT false true
bash $TARGET_SCRIPT TreeMap put 11 PLIOPT false true

bash $TARGET_SCRIPT TreeSet remove 11 PLIOPT false true
bash $TARGET_SCRIPT TreeSet add 11 PLIOPT false true

bash $TARGET_SCRIPT Schedule quantumExpire 11 PLIOPT false true
bash $TARGET_SCRIPT Schedule addProcess 25 PLIOPT false true
bash $TARGET_SCRIPT Schedule finishAllProcesses 20 PLIOPT false true