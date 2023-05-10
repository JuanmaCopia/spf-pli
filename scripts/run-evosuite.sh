#/bin/bash

# Declare subjects
declare -a subjects=(
	"heapsolving.avltree.AvlTree_EvoSuite"
	"heapsolving.binomialheap.BinomialHeap_EvoSuite"
	"heapsolving.combatantstatistic.CombatantStatistic_EvoSuite"
	"heapsolving.dictionaryinfo.DictionaryInfo_EvoSuite"
	"heapsolving.hashmap.HashMap_EvoSuite"
	"heapsolving.linkedlist.LinkedList_EvoSuite"
	"heapsolving.schedule.Schedule_EvoSuite"
	"heapsolving.sqlfilterclauses.SQLFilterClauses_EvoSuite"
	"heapsolving.template.Template_EvoSuite"
	"heapsolving.transportstats.TransportStats_EvoSuite"
	"heapsolving.treemap.TreeMap"
	"heapsolving.treeset.TreeSet"
)

EVOSUITE_BUDGET=60

full_cp="build/examples:lib/symsolve.jar"

# Run evosuite for each subject
for subject in "${subjects[@]}"
do
	target_class=$subject
	echo "> Running EvoSuite for: $target_class"
	java -jar $EVOSUITE_JAR -class $target_class -projectCP $full_cp -Dsearch_budget=$EVOSUITE_BUDGET
	echo "finished!"

	# Save output in a specific location.
	single_class_name="${target_class##*.}"
	echo "tests saved in evosuite-tests"
	echo "report saved in evosuite-report/$single_class_name-statistics.csv"
	mv evosuite-report/statistics.csv evosuite-report/$single_class_name-statistics.csv
	echo ""
done

