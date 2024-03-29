#!/bin/bash

# Constants
TIMEOUT=3610 # 1 hours and 10 seconds

# Arguments
CLASS_NAME=$1
METHOD=$2
TECHNIQUE=$3
MINSCOPE=$4
MAXSCOPE=$5
SRC_FOLDER=$6
PACKAGE=$7
CHECKPATH=$8
GENTESTS=$9

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TARGET_SCRIPT="${SCRIPT_DIR}/run_script.sh"

OUTDIR="output/stdout"
mkdir output 2>/dev/null
mkdir $OUTDIR 2>/dev/null

echo ""
echo "---- $TECHNIQUE ----"

retn_code=0

for ((i = $MINSCOPE; i <= $MAXSCOPE; i++)); do
    if [ $retn_code -eq 0 ]; then
        echo "Running $CLASS_NAME.$METHOD with $TECHNIQUE for scope $i"
        bash $TARGET_SCRIPT $CLASS_NAME $TECHNIQUE $i $METHOD $TIMEOUT $SRC_FOLDER $PACKAGE $CHECKPATH $GENTESTS > "${OUTDIR}/${CLASS_NAME}_${METHOD}_${i}-${TECHNIQUE}"
        retn_code=$?
    else
        echo "Stopping execution due timeout"
        exit 0
    fi
done