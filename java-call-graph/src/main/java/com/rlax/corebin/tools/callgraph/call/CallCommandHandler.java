package com.rlax.corebin.tools.callgraph.call;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InvokeInstruction;

/**
 * 方法调用指令处理器
 * @author Rlax
 *
 */
public interface CallCommandHandler {

    /**
     * 调用指令处理器
     * @param callerJavaClass 调用者类
     * @param callerMethod 调用者方法
     * @param invokeInstruction 调用指令
     * @return 调用信息
     */
    MethodCallInfo handleCallCommand(JavaClass callerJavaClass, Method callerMethod, InvokeInstruction invokeInstruction);

}
