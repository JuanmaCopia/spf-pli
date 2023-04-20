#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"



TARGET_SCRIPT="${SCRIPT_DIR}/combatantstatistic.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/dictionaryinfo.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/schedule.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/sqlfilterclauses.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"


TARGET_SCRIPT="${SCRIPT_DIR}/transportstats.sh"
bash $TARGET_SCRIPT
echo "________________________________________________________________________"

TARGET_SCRIPT="${SCRIPT_DIR}/template.sh"
bash $TARGET_SCRIPT
