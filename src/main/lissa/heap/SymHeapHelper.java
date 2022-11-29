package lissa.heap;

import java.util.HashMap;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.heap.visitors.SymbolicOutputHeapVisitor;

public class SymHeapHelper {
    
    public static SymbolicInputHeap getSymbolicInputHeap() {
        HeapChoiceGenerator heapCG = VM.getVM().getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
        return heapCG.getCurrentSymInputHeap();
    }

    public static SymbolicInputHeap getSymbolicInputHeap(VM vm) {
        HeapChoiceGenerator heapCG = vm.getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
        return heapCG.getCurrentSymInputHeap();
    }

    public static ThreadInfo getCurrentThread() {
        return ThreadInfo.getCurrentThread();
    }
    
    public static PathCondition getPathCondition() {
        return PathCondition.getPC(VM.getVM());
    }

    public static PathCondition getPathCondition(VM vm) {
        return PathCondition.getPC(vm);
    }
    
    public static Integer getSolution(SymbolicInteger symbolicInteger, PathCondition pathCondition) {
      int solution = 0;
      if (pathCondition != null) {
          if (!PathCondition.flagSolved)
              pathCondition.solveOld();
          long val = symbolicInteger.solution();
          if (val != SymbolicInteger.UNDEFINED)
              solution = (int) val;
      }
      return solution;
  }
    
    
    public static void acceptBFS(int rootIndex, SymbolicOutputHeapVisitor visitor) {
        HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
        HashMap<ClassInfo, Integer> maxIdMap = new HashMap<ClassInfo, Integer>();

        ThreadInfo ti = VM.getVM().getCurrentThread();
        ElementInfo rootElementInfo = ti.getElementInfo(rootIndex);
        ClassInfo rootClass = rootElementInfo.getClassInfo();

        idMap.put(rootIndex, 0);
        maxIdMap.put(rootClass, 0);

        LinkedList<Integer> worklist = new LinkedList<Integer>();
        worklist.add(rootIndex);

        while (!worklist.isEmpty()) {
            int currentObjRef = worklist.removeFirst();
            int currentObjID = idMap.get(currentObjRef);
            ElementInfo elementInfo = ti.getElementInfo(currentObjRef);
            ClassInfo ownerObjectClass = elementInfo.getClassInfo();

//            visitor.setCurrentOwner(ownerObjectClass, currentObjID);


            if (currentObjRef != rootIndex)
                visitor.setCurrentOwner(ownerObjectClass, currentObjID + 1);
            else
                visitor.setCurrentOwner(ownerObjectClass, currentObjID);

            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();
            for (int i = 0; i < instanceFields.length; i++) {
                FieldInfo field = instanceFields[i];
                ClassInfo fieldClass = field.getTypeClassInfo();

                visitor.setCurrentField(fieldClass, field);

                if (visitor.isIgnoredField()) {
                    // System.out.println("Ignored field: " + field.getName());
                    // System.out.println("type: " + fieldClass.getSimpleName());
                    continue;
                }

                if (field.isReference() && !field.getType().equals("java.lang.String")) {
                	Object attr = elementInfo.getFieldAttr(field);
                	int fieldIndex = elementInfo.getReferenceField(field);
                    //Integer fieldIndex = getReferenceField(currentObjRef, field);
                    if (attr != null) {
                        visitor.visitedSymbolicReferenceField();
                    } else if (fieldIndex == MJIEnv.NULL) {
                        visitor.visitedNullReferenceField();
                    } else if (idMap.containsKey(fieldIndex)) { // previously visited object
                        visitor.visitedExistentReferenceField(idMap.get(fieldIndex) + 1);
                    } else { // first time visited
                        int id = 0;
                        if (maxIdMap.containsKey(fieldClass))
                            id = maxIdMap.get(fieldClass) + 1;

                        idMap.put(fieldIndex, id);
                        maxIdMap.put(fieldClass, id);
                        visitor.visitedNewReferenceField(id + 1);
                        worklist.add(fieldIndex);
                    }
                } else {
                	visitor.visitedSymbolicPrimitiveField(field);
                }
                visitor.resetCurrentField();
            }
            visitor.resetCurrentOwner();
        }
    }

}
