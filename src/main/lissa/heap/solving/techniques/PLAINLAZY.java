package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;

public class PLAINLAZY extends LIBasedStrategy {

    HashMap<Integer, Integer> fieldGetCount = new HashMap<Integer, Integer>();

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        fieldGetCount.clear();
        return nextInstruction;
    }

    public boolean isInfiniteLoopHeuristic(FieldInfo field, Object attr, int ownerObjectRef) {
        ClassInfo typeClassInfo = field.getTypeClassInfo();
        String fullClassName = typeClassInfo.getName();

        if (field.isReference() && attr == null && isClassInBounds(fullClassName)
                && reachedGETFIELDLimit(ownerObjectRef)) {
            return true;
        }
        return false;
    }

    public boolean reachedGETFIELDLimit(int objRef) {
        if (!fieldGetCount.containsKey(objRef))
            fieldGetCount.put(objRef, 0);
        Integer count = fieldGetCount.get(objRef);

        if (count >= config.getFieldLimit) {
            fieldGetCount.clear();
            return true;
        }
        fieldGetCount.put(objRef, count + 1);
        return false;
    }

    public void resetGetFieldCount() {
        fieldGetCount.clear();
    }

}
