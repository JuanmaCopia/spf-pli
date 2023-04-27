SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_case_study.sh"


bash $TARGET_SCRIPT AvlTree insert 9 PLIOPT false true
bash $TARGET_SCRIPT AvlTree remove 9 PLIOPT false true

bash $TARGET_SCRIPT BinomialHeap extractMinFixed 13 PLIOPT false true
bash $TARGET_SCRIPT BinomialHeap extractMinBugged 13 PLIOPT false true
bash $TARGET_SCRIPT BinomialHeap insert 13 PLIOPT false true

bash $TARGET_SCRIPT CombatantStatistic addData 8 PLIOPT false true
bash $TARGET_SCRIPT CombatantStatistic ensureTypExists 25 PLIOPT false true