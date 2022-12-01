#/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
SCRIPT_DIR="${SCRIPT_DIR}/.."

export LD_LIBRARY_PATH="lib:../jpf-symbc/lib"

CP="build/main:build/examples:build/peers:build/tests:build/annotations:build/classes:build/:lib/*:../jpf-core/build/main:../jpf-core/build/examples:../jpf-core/build/peers:../jpf-core/build/tests:../jpf-core/build/annotations:../jpf-core/build/classes:../jpf-core/build:../jpf-core/lib/*:../jpf-symbc/build/main:../jpf-symbc/build/examples:../jpf-symbc/build/peers:../jpf-symbc/build/tests:../jpf-symbc/build/annotations:../jpf-symbc/build/classes:../jpf-symbc/build:../jpf-symbc/lib/*"

# Arguments
CLASS_NAME=$1
STRATEGY=$2
SCOPE=$3
METHOD=$4
TIMEOUT=$5
INTERVAL=$6
SRC_FOLDER=$7
PACKAGE=$8


CONFIG_FOLDER="$SCRIPT_DIR/$SRC_FOLDER"
PATH_CONFIG_FILE="${CONFIG_FOLDER}/${CLASS_NAME}.jpf"

TARGET="${PACKAGE}.${METHOD,,}.${CLASS_NAME}Main"
STRATEGY=${STRATEGY^^} # To upper case


# Set values in config file
sed -i -E "s/target.*/target = $TARGET/g" $PATH_CONFIG_FILE
sed -i -E "s/method.*/method = ${METHOD}/g" $PATH_CONFIG_FILE
sed -i -E "s/heapsolving\.strategy.*/heapsolving\.strategy = $STRATEGY/g" $PATH_CONFIG_FILE
sed -i -E "s/symbolic\.scope.*/symbolic\.scope = $SCOPE/g" $PATH_CONFIG_FILE


finishedOK=false
timeout -k 10 $TIMEOUT java -Dfile.encoding=UTF-8 -Xms4096m -Xmx4096m -Xss100m -ea -cp $CP gov.nasa.jpf.tool.RunJPF $PATH_CONFIG_FILE
if [ $? -eq 0 ]; then
    finishedOK=true
fi


# Reset config file values
TARGET="${PACKAGE}.METHOD.${CLASS_NAME}Main"
sed -i -E "s/target.*/target = $TARGET/g" $PATH_CONFIG_FILE
sed -i -E "s/method.*/method = METHOD/g" $PATH_CONFIG_FILE
sed -i -E "s/heapsolving\.strategy.*/heapsolving\.strategy = HEAP_SOLVING_STRATEGY/g" $PATH_CONFIG_FILE
sed -i -E "s/symbolic\.scope.*/symbolic\.scope = MAX_SCOPE/g" $PATH_CONFIG_FILE


if [ "$finishedOK" = false ] ; then
    echo "TIMEOUT"
    exit 42
else
    echo "OK"
fi