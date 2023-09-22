package com.rlax.corebin.tools.callgraph.call;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.InstructionHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * CallCommandHandlerDispatcher
 *
 * @author Rlax
 * @date 2023/09/22
 */
public class CallCommandHandlerDispatcher {

    private final List<CallCommandHandler> handlers = new ArrayList<>();

    public void addHandler(CallCommandHandler handler) {
        if (handlers.isEmpty()) {
            handlers.add(handler);
            return;
        }

        CallCommandHandler pre = handlers.get(handlers.size() - 1);
        pre.setNextCallCommandHandler(handler);
        handlers.add(handler);
    }

    public MethodCallInfo doDispatch(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle) {
        if (handlers.isEmpty()) {
            return null;
        }
        return handlers.get(0).handle(callerJavaClass, callerMethod, instructionHandle);
    }
}
