#/bin/bash

SRC_FOLDER="src/examples/heapsolving/combatantstatistic"
PACKAGE="heapsolving.combatantstatistic"


CLASS_NAME="CombatantStatistic"
echo "===============================  Class: $CLASS_NAME  ================================="
MINSCOPE=1
MAXSCOPE=50


METHOD="addData"
echo "-------- Method: $METHOD --------"
bash scripts/run_upto.sh $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSAM $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSANOSB $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE

METHOD="ensureTypExists"
echo "-------- Method: $METHOD --------"
bash scripts/run_upto.sh $CLASS_NAME $METHOD LIHYBRID $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD DRIVER $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD IFREPOK $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSA $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSAM $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
bash scripts/run_upto.sh $CLASS_NAME $METHOD LISSANOSB $MINSCOPE $MAXSCOPE $SRC_FOLDER $PACKAGE
