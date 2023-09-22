import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.rlax.corebin.tools.callgraph.common.CallType;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/19
 */
public class InvokeInstructionTest {

    @Test
    void handleINVOKEDYNAMIC() {
        String fullCallClassName = "bean.TestBean";

        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

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

            Console.log(callType);

            InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();
            if (opCode == Const.INVOKEDYNAMIC) {
                INVOKEDYNAMIC invokedynamic = (INVOKEDYNAMIC) invokeInstruction;

                String calleeClassName = invokedynamic.getType(constantPoolGen).toString();
                String calleeMethodName = invokedynamic.getMethodName(constantPoolGen);
                Type[] calleeArguments = invokedynamic.getArgumentTypes(constantPoolGen);

                // 获取被调用类名
                String calledClassName = invokeInstruction.getClassName(constantPoolGen);
                // 获取被调用方法
                String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                String calledSignature = invokeInstruction.getSignature(constantPoolGen);
                String returnType = invokeInstruction.getReturnType(constantPoolGen).getClassName();

                Console.log(calleeClassName);
                Console.log(calleeMethodName);
                Console.log(calleeArguments);

                Console.log(calledClassName);
                Console.log(calledMethodName);
                Console.log(calledSignature);
                Console.log(returnType);

                // 判断是否需要为Lambda表达式
                Constant constant = constantPoolGen.getConstant(invokedynamic.getIndex());
                Console.log(constant);
                if (constant instanceof ConstantInvokeDynamic) {
                    Console.log("Lambda表达式");

                    int index = ((ConstantInvokeDynamic) constant).getBootstrapMethodAttrIndex();
                    BootstrapMethod bootstrapMethod = null;
                    for (Attribute attribute : callerJavaClass.getAttributes()) {
                        if (attribute instanceof BootstrapMethods) {
                            BootstrapMethods bootstrapMethods = (BootstrapMethods) attribute;
                            BootstrapMethod[] bootstrapMethodArray = bootstrapMethods.getBootstrapMethods();
                            if (bootstrapMethodArray != null && bootstrapMethodArray.length > index) {
                                bootstrapMethod = bootstrapMethodArray[index];
                                Console.log(bootstrapMethod);
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
                            System.err.println("### 不满足instanceof ConstantCP " + constantCP.getClass().getName());
                            continue;
                        }

                        ConstantCP constantClassAndMethod = (ConstantCP) constantCP;
                        String className = callerJavaClass.getConstantPool().getConstantString(constantClassAndMethod.getClassIndex(), Const.CONSTANT_Class);
                        className = Utility.compactClassName(className, false);

                        Constant constantNAT = callerJavaClass.getConstantPool().getConstant(constantClassAndMethod.getNameAndTypeIndex());
                        if (!(constantNAT instanceof ConstantNameAndType)) {
                            System.err.println("### 不满足instanceof ConstantNameAndType " + constantNAT.getClass().getName());
                            continue;
                        }
                        ConstantNameAndType constantNameAndType = (ConstantNameAndType) constantNAT;
                        String methodName = callerJavaClass.getConstantPool().constantToString(constantNameAndType.getNameIndex(), Const.CONSTANT_Utf8);
                        String methodArgs = callerJavaClass.getConstantPool().constantToString(constantNameAndType.getSignatureIndex(), Const.CONSTANT_Utf8);

                        Console.log(className);
                        Console.log(methodName);
                        Console.log(methodArgs);

                        // 获取 lambda 方法
//                        java.lang.reflect.Method m2 = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), methodName, String.class);
//                        Method method2 = BaceUtil.findMethodByReflectMethod(callerJavaClass, m2);
//
//                        Console.log(method2.getName());
                    }
                }
            }

        }
    }

    @Test
    void handleINVOKESTATIC() {
        String fullCallClassName = "bean.TestBean";

        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

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
            if (opCode == Const.INVOKESTATIC) {
                INVOKESTATIC invokestatic = (INVOKESTATIC) invokeInstruction;

                String calleeClassName = invokestatic.getReferenceType(constantPoolGen).toString();
                String calleeMethodName = invokestatic.getMethodName(constantPoolGen);
                Type[] calleeArguments = invokestatic.getArgumentTypes(constantPoolGen);

                // 获取被调用类名
                String calledClassName = invokeInstruction.getClassName(constantPoolGen);
                // 获取被调用方法
                String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                String calledSignature = invokeInstruction.getSignature(constantPoolGen);
                String returnType = invokeInstruction.getReturnType(constantPoolGen).getClassName();

                Console.log(calleeClassName);
                Console.log(calleeMethodName);
                Console.log(calleeArguments);

                Console.log(calledClassName);
                Console.log(calledMethodName);
                Console.log(calledSignature);
                Console.log(returnType);
            }
        }
    }


    @Test
    void handleINVOKESPECIAL() {
        String fullCallClassName = "bean.TestBean";

        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

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
            if (opCode == Const.INVOKESPECIAL) {
                INVOKESPECIAL invokespecial = (INVOKESPECIAL) invokeInstruction;

                String calleeClassName = invokespecial.getReferenceType(constantPoolGen).toString();
                String calleeMethodName = invokespecial.getMethodName(constantPoolGen);
                Type[] calleeArguments = invokespecial.getArgumentTypes(constantPoolGen);

                // 获取被调用类名
                String calledClassName = invokeInstruction.getClassName(constantPoolGen);
                // 获取被调用方法
                String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                String calledSignature = invokeInstruction.getSignature(constantPoolGen);

                Console.log(calleeClassName);
                Console.log(calleeMethodName);
                Console.log(calleeArguments);

                Console.log(calledClassName);
                Console.log(calledMethodName);
                Console.log(calledSignature);
            }

        }
    }


    @Test
    void handleINVOKEINTERFACE() {
        String fullCallClassName = "bean.TestBean";

        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

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
            if (opCode == Const.INVOKEINTERFACE) {
                INVOKEINTERFACE invokeinterface = (INVOKEINTERFACE) invokeInstruction;

                String calleeClassName = invokeinterface.getReferenceType(constantPoolGen).toString();
                String calleeMethodName = invokeinterface.getMethodName(constantPoolGen);
                Type[] calleeArguments = invokeinterface.getArgumentTypes(constantPoolGen);

                // 获取被调用类名
                String calledClassName = invokeInstruction.getClassName(constantPoolGen);
                // 获取被调用方法
                String calledMethodName = invokeInstruction.getMethodName(constantPoolGen);
                String calledSignature = invokeInstruction.getSignature(constantPoolGen);

                Console.log(calleeClassName);
                Console.log(calleeMethodName);
                Console.log(calleeArguments);

                Console.log(calledClassName);
                Console.log(calledMethodName);
                Console.log(calledSignature);
            }

        }
    }
}
