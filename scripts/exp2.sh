#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $TARGET_SCRIPT DictionaryInfo addField 9 PLIOPT false true
bash $TARGET_SCRIPT DictionaryInfo getField 10 PLIOPT false true

bash $TARGET_SCRIPT HashMap remove 5 PLIOPT false true
bash $TARGET_SCRIPT HashMap put 5 PLIOPT false true

bash $TARGET_SCRIPT LinkedList remove 25 PLIOPT false true
bash $TARGET_SCRIPT LinkedList add 25 PLIOPT false true
