#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

bash "$SCRIPT_DIR/avltree.sh"
bash "$SCRIPT_DIR/transportstats.sh"
bash "$SCRIPT_DIR/dictionaryinfo.sh"
bash "$SCRIPT_DIR/template.sh"
bash "$SCRIPT_DIR/sqlfilterclauses.sh"
bash "$SCRIPT_DIR/combatantstatistic.sh"


