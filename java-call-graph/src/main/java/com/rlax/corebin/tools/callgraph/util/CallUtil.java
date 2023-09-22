package com.rlax.corebin.tools.callgraph.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.rlax.corebin.tools.callgraph.common.CallType;
import com.rlax.corebin.tools.callgraph.common.CommonConstants;
import com.rlax.corebin.tools.callgraph.exception.CallGraphGenerateException;
import com.rlax.corebin.tools.callgraph.model.CallClass;
import com.rlax.corebin.tools.callgraph.model.CallItem;
import com.rlax.corebin.tools.callgraph.model.CallMethod;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 工具类
 *
 * @author Rlax
 * @date 2023/09/13
 */
public class CallUtil {

    private final static ExecutorService EXECUTOR = ThreadUtil.newSingleExecutor();

    public static List<CallItem> generateModel(String fullCallClassName, String methodName, Class<?>... paramClassType) {
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);

        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), methodName, paramClassType);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);
        if (method == null) {
            throw new CallGraphGenerateException(1, "method can not find");
        }

        return generateModel(callerJavaClass, method);
    }




    public static List<CallItem> generateModel(String fullCallClassName, String fullMethodString) {
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        Method method = BaceUtil.findMethodByFullName(callerJavaClass, fullMethodString);
        if (method == null) {
            throw new CallGraphGenerateException(1, "method can not find");
        }

        return generateModel(callerJavaClass, method);
    }

    public static List<CallItem> generateModel(JavaClass callerJavaClass, Method method) {
        List<CallItem> callItemList = new ArrayList<>();

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
            String calledClassName;
            String calledMethodName;
            Type[] calledArguments;

            switch (opCode) {
                // 调用动态执行
                case Const.INVOKEDYNAMIC:
                    calledClassName = invokeInstruction.getType(constantPoolGen).toString();
                    calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                    calledArguments = invokeInstruction.getArgumentTypes(constantPoolGen);
                    break;
                // 接口
                case Const.INVOKEINTERFACE:
                // 调用私有方法，父类方法(super.)，类构造器方法
                case Const.INVOKESPECIAL:
                // 静态方法
                case Const.INVOKESTATIC:
                // 所有虚方法
                case Const.INVOKEVIRTUAL:
                    calledClassName = invokeInstruction.getReferenceType(constantPoolGen).toString();
                    calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                    calledArguments = invokeInstruction.getArgumentTypes(constantPoolGen);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + opCode);
            }

            if (calledMethodName.equals(CommonConstants.METHOD_NAME_INIT)) {
                continue;
            }

            JavaClass calledJavaClass = BaceUtil.loadClass(calledClassName);
            Method calledJavaMethod = BaceUtil.findMethod(calledJavaClass, calledMethodName, calledArguments);

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
