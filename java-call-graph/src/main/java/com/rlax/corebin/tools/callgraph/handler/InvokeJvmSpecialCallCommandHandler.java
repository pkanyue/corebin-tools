package com.rlax.corebin.tools.callgraph.handler;

import com.rlax.corebin.tools.callgraph.call.MethodCallInfo;
import com.rlax.corebin.tools.callgraph.common.CallType;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

/**
 * 调用指令处理器
 *
 * @author Rlax
 * @date 2023/09/19
 */
@Slf4j
public class InvokeJvmSpecialCallCommandHandler extends AbstractCallCommandHandler {

    @Override
    public boolean support(CallType callType) {
        return CallType.JVM_INVOKE_SPECIAL.equals(callType);
    }

    @Override
    public MethodCallInfo handleCallCommand(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle, CallType callType, String callOrder) {
        InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        LineNumberTable lineNumberTable = callerMethod.getLineNumberTable();

        // 调用类
        String calledClassName = invokeInstruction.getReferenceType(constantPoolGen).getClassName();
        String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
        Type[] calledArguments = invokeInstruction.getArgumentTypes(constantPoolGen);
        Type calledReturnType = invokeInstruction.getReturnType(constantPoolGen);

        return MethodCallInfo.builder()
                .callerClassName(callerJavaClass.getClassName())
                .callerMethodName(callerMethod.getName())
                .callerMethodArguments(callerMethod.getArgumentTypes())
                .callerMethodReturnType(callerMethod.getReturnType())
                .jvmOpCode(invokeInstruction.getOpcode())
                .callType(CallType.JVM_INVOKE_SPECIAL)
                .callLineNum(lineNumberTable.getSourceLine(instructionHandle.getPosition()))
                .calledClassName(calledClassName)
                .calledMethodName(calledMethodName)
                .calledMethodArguments(calledArguments)
                .calledMethodReturnType(calledReturnType)
                .callOrder(callOrder)
                .build();
    }

}
