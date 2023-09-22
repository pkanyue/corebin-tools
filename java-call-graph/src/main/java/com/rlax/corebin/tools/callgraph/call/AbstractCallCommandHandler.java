package com.rlax.corebin.tools.callgraph.call;

import com.rlax.corebin.tools.callgraph.common.CallType;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;

/**
 * @author Rlax
 * @date 2023/09/21
 */
public abstract class AbstractCallCommandHandler implements CallCommandHandler {

    protected CallCommandHandler next;

    @Override
    public void setNextCallCommandHandler(CallCommandHandler nextCallCommandHandler) {
        this.next = nextCallCommandHandler;
    }

    @Override
    public MethodCallInfo handle(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle) {
        InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();
        short opCode = invokeInstruction.getOpcode();
        CallType callType = CallType.getByCode(String.valueOf(opCode));
        if (support(callType)) {
            return handleCallCommand(callerJavaClass, callerMethod, instructionHandle, callType);
        } else {
            if (next == null) {
                return null;
            }
            return next.handle(callerJavaClass, callerMethod, instructionHandle);
        }
    }

    /**
     * 判断调用类型是否支持
     * @param callType 调用类型
     * @return 是否支持
     */
    public abstract boolean support(CallType callType);
}
