package com.rlax.corebin.tools.callgraph.call;

import com.rlax.corebin.tools.callgraph.common.CallType;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InstructionHandle;

/**
 * 方法调用指令处理器
 * @author Rlax
 *
 */
public interface CallCommandHandler {

    void setNextCallCommandHandler(CallCommandHandler next);
    MethodCallInfo handle(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle);

    /**
     * 调用指令处理器
     * @param callerJavaClass 调用者类
     * @param callerMethod 调用者方法
     * @param instructionHandle 调用指令
     * @return 调用信息
     */
    MethodCallInfo handleCallCommand(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle, CallType callType);

}
