package com.rlax.corebin.tools.callgraph.handler;

import com.rlax.corebin.tools.callgraph.call.MethodCallInfo;
import com.rlax.corebin.tools.callgraph.common.CallType;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Type;

/**
 * 动态调用指令处理器
 *
 * @author Rlax
 * @date 2023/09/19
 */
@Slf4j
public class InvokeJvmDynamicCallCommandHandler extends AbstractCallCommandHandler {

    @Override
    public boolean support(CallType callType) {
        return CallType.JVM_INVOKE_DYNAMIC.equals(callType);
    }

    @Override
    public MethodCallInfo handleCallCommand(JavaClass callerJavaClass, Method callerMethod, InstructionHandle instructionHandle, CallType callType, String callOrder) {
        InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        LineNumberTable lineNumberTable = callerMethod.getLineNumberTable();

        // 调用类
        String calledClassName = invokeInstruction.getType(constantPoolGen).getClassName();
        String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
        Type[] calledArguments = invokeInstruction.getArgumentTypes(constantPoolGen);
        Type calledReturnType = invokeInstruction.getReturnType(constantPoolGen);

        // 判断是否需要为Lambda表达式
        Constant constant = constantPoolGen.getConstant(invokeInstruction.getIndex());
        if (constant instanceof ConstantInvokeDynamic) {
            int index = ((ConstantInvokeDynamic) constant).getBootstrapMethodAttrIndex();
            BootstrapMethod bootstrapMethod = null;
            for (Attribute attribute : callerJavaClass.getAttributes()) {
                if (attribute instanceof BootstrapMethods) {
                    BootstrapMethods bootstrapMethods = (BootstrapMethods) attribute;
                    BootstrapMethod[] bootstrapMethodArray = bootstrapMethods.getBootstrapMethods();
                    if (bootstrapMethodArray != null && bootstrapMethodArray.length > index) {
                        bootstrapMethod = bootstrapMethodArray[index];
                    }
                }
            }

            for (int argIndex : bootstrapMethod.getBootstrapArguments()) {
                Constant constantArg = constantPoolGen.getConstant(argIndex);
                if (!(constantArg instanceof ConstantMethodHandle)) {
                    continue;
                }

                ConstantMethodHandle constantMethodHandle = (ConstantMethodHandle) constantArg;
                Constant constantCP = constantPoolGen.getConstant(constantMethodHandle.getReferenceIndex());
                if (!(constantCP instanceof ConstantCP)) {
                    continue;
                }

                ConstantCP constantClassAndMethod = (ConstantCP) constantCP;
                String className = callerJavaClass.getConstantPool().getConstantString(constantClassAndMethod.getClassIndex(), Const.CONSTANT_Class);
                className = Utility.compactClassName(className, false);

                Constant constantNAT = callerJavaClass.getConstantPool().getConstant(constantClassAndMethod.getNameAndTypeIndex());
                if (!(constantNAT instanceof ConstantNameAndType)) {
                    continue;
                }
                ConstantNameAndType constantNameAndType = (ConstantNameAndType) constantNAT;
                String methodName = callerJavaClass.getConstantPool().constantToString(constantNameAndType.getNameIndex(), Const.CONSTANT_Utf8);
                String methodSignature = callerJavaClass.getConstantPool().constantToString(constantNameAndType.getSignatureIndex(), Const.CONSTANT_Utf8);

                calledClassName = className;
                calledMethodName = methodName;
                calledArguments = Type.getArgumentTypes(methodSignature);
                calledReturnType = Type.getReturnType(methodSignature);
            }
        }

        return MethodCallInfo.builder()
                .callerClassName(callerJavaClass.getClassName())
                .callerMethodName(callerMethod.getName())
                .callerMethodArguments(callerMethod.getArgumentTypes())
                .callerMethodReturnType(callerMethod.getReturnType())
                .jvmOpCode(invokeInstruction.getOpcode())
                .callType(CallType.JVM_INVOKE_DYNAMIC_LAMBDA)
                .callLineNum(lineNumberTable.getSourceLine(instructionHandle.getPosition()))
                .calledClassName(calledClassName)
                .calledMethodName(calledMethodName)
                .calledMethodArguments(calledArguments)
                .calledMethodReturnType(calledReturnType)
                .callOrder(callOrder)
                .build();
    }

}
