#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"


TARGET_SCRIPT="${SCRIPT_DIR}/template.sh"
bash $TARGET_SCRIPT
