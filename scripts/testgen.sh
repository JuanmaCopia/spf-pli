#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $TARGET_SCRIPT AvlTree insert 9 PLIOPT false true
bash $TARGET_SCRIPT AvlTree remove 9 PLIOPT false true

bash $TARGET_SCRIPT BinomialHeap extractMinFixed 13 PLIOPT false true
bash $TARGET_SCRIPT BinomialHeap extractMinBugged 13 PLIOPT false true
bash $TARGET_SCRIPT BinomialHeap insert 13 PLIOPT false true

bash $TARGET_SCRIPT CombatantStatistic addData 8 PLIOPT false true
bash $TARGET_SCRIPT CombatantStatistic ensureTypExists 25 PLIOPT false true

bash $TARGET_SCRIPT DictionaryInfo addField 9 PLIOPT false true
bash $TARGET_SCRIPT DictionaryInfo getField 10 PLIOPT false true

bash $TARGET_SCRIPT HashMap remove 5 PLIOPT false true
bash $TARGET_SCRIPT HashMap put 5 PLIOPT false true

bash $TARGET_SCRIPT LinkedList remove 25 PLIOPT false true
bash $TARGET_SCRIPT LinkedList add 25 PLIOPT false true

bash $TARGET_SCRIPT SQLFilterClauses get 2 PLIOPT false true
bash $TARGET_SCRIPT SQLFilterClauses put 2 PLIOPT false true

bash $TARGET_SCRIPT Template addParameter 4 PLIOPT false true
bash $TARGET_SCRIPT Template getParameter 4 PLIOPT false true

bash $TARGET_SCRIPT TransportStats bytesRead 11 PLIOPT false true
bash $TARGET_SCRIPT TransportStats bytesWritten 9 PLIOPT false true

bash $TARGET_SCRIPT TreeMap remove 11 PLIOPT false true
bash $TARGET_SCRIPT TreeMap put 11 PLIOPT false true

bash $TARGET_SCRIPT TreeSet remove 11 PLIOPT false true
bash $TARGET_SCRIPT TreeSet add 11 PLIOPT false true

bash $TARGET_SCRIPT Schedule quantumExpire 11 PLIOPT false true
bash $TARGET_SCRIPT Schedule addProcess 25 PLIOPT false true
bash $TARGET_SCRIPT Schedule finishAllProcesses 20 PLIOPT false true

