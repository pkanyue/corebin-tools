package com.rlax.corebin.tools.callgraph.call;

import cn.hutool.core.lang.Console;
import com.rlax.corebin.tools.callgraph.common.CallType;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

/**
 * 动态调用指令处理器
 *
 * @author Rlax
 * @date 2023/09/19
 */
@Slf4j
public class InvokeDynamicCallCommandHandler implements CallCommandHandler {

    @Override
    public MethodCallInfo handleCallCommand(JavaClass callerJavaClass, Method callerMethod, InvokeInstruction invokeInstruction) {
        short opCode = invokeInstruction.getOpcode();
        if (Const.INVOKEDYNAMIC != opCode) {
            log.error("not support opCode: {}", opCode);
            return null;
        }

        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());

        String calledClassName = invokeInstruction.getType(constantPoolGen).getClassName();
        String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
        Type[] calledArguments = invokeInstruction.getArgumentTypes(constantPoolGen);
        CallType callType = CallType.getByCode(String.valueOf(opCode));

        // 判断是否需要为Lambda表达式
        Constant constant = constantPoolGen.getConstant(invokeInstruction.getIndex());
        Console.log(constant);
        if (constant instanceof ConstantInvokeDynamic) {

        }

        return null;
    }

}
