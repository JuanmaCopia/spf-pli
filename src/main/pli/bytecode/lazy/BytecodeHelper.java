package pli.bytecode.lazy;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

public class BytecodeHelper {

    public static InvokeStaticInstruction createInvokeStaticInstruction(ClassInfo classInfo,
            String staticMethodSignature, Instruction current) {
        MethodInfo repokMI = classInfo.getMethod(staticMethodSignature, false);

        String clsName = repokMI.getClassInfo().getName();
        String mthName = repokMI.getName();
        String signature = repokMI.getSignature();

        return new InvokeStaticInstruction(clsName, mthName, signature, current);
    }

}
