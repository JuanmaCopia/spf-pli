#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $TARGET_SCRIPT SQLFilterClauses get 2 PLIOPT false true
bash $TARGET_SCRIPT SQLFilterClauses put 2 PLIOPT false true

bash $TARGET_SCRIPT Template addParameter 4 PLIOPT false true
bash $TARGET_SCRIPT Template getParameter 4 PLIOPT false true

bash $TARGET_SCRIPT TransportStats bytesRead 11 PLIOPT false true
bash $TARGET_SCRIPT TransportStats bytesWritten 9 PLIOPT false true