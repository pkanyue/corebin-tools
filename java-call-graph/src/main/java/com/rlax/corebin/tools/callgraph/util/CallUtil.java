package com.rlax.corebin.tools.callgraph.util;

import com.rlax.corebin.tools.callgraph.common.CallType;
import com.rlax.corebin.tools.callgraph.common.CommonConstants;
import com.rlax.corebin.tools.callgraph.exception.CallGraphGenerateException;
import com.rlax.corebin.tools.callgraph.model.CallClass;
import com.rlax.corebin.tools.callgraph.model.CallItem;
import com.rlax.corebin.tools.callgraph.model.CallMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 *
 * @author Rlax
 * @date 2023/09/13
 */
public class CallUtil {

    public static List<CallItem> generateModel(String fullCallClassName, String fullMethodString) {
        List<CallItem> callItemList = new ArrayList<>();

        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        Method method = BaceUtil.findMethodByFullName(callerJavaClass, fullMethodString);
        if (method == null) {
            throw new CallGraphGenerateException(1, "method can not find");
        }

        CallClass callerClass = CallClass.create(callerJavaClass);
        CallMethod callerMethod = CallMethod.create(callerClass, method);

        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, callerJavaClass.getClassName(), constantPoolGen);
        InstructionList instructionList = methodGen.getInstructionList();
        LineNumberTable lineNumberTable = method.getLineNumberTable();

        for (InstructionHandle instructionHandle : instructionList) {
            short opCode = instructionHandle.getInstruction().getOpcode();
            CallType callType = CallType.getByCode(String.valueOf(opCode));
            if (callType == null) {
                continue;
            }

            InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();
            // 获取被调用类名
            String calledClassName = invokeInstruction.getClassName(constantPoolGen);
            // 获取被调用方法
            String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
            String calledSignature = invokeInstruction.getSignature(constantPoolGen);

            if (calledMethodName.equals(CommonConstants.METHOD_NAME_INIT)) {
                continue;
            }

            JavaClass calledJavaClass = BaceUtil.loadClass(calledClassName);
            Method calledJavaMethod = BaceUtil.findMethod(calledJavaClass, calledMethodName, calledSignature);

            CallClass calledClass = CallClass.create(calledJavaClass);
            CallMethod calledMethod = CallMethod.create(calledClass, calledJavaMethod);

            CallItem callItem = CallItem.builder()
                    .callerClass(callerClass)
                    .callerMethod(callerMethod)
                    .calledClass(calledClass)
                    .calledMethod(calledMethod)
                    .callLineNum(lineNumberTable.getSourceLine(instructionHandle.getPosition()))
                    .callType(callType)
                    .build();

            callItemList.add(callItem);
        }

        return callItemList;
    }

}
