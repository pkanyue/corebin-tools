import cn.hutool.core.lang.Console;
import com.rlax.corebin.tools.callgraph.common.CommonConstants;
import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/07
 */
public class CallTraceTest {

    @DisplayName("打印调用链")
    @Test
    void callTrace() throws ClassNotFoundException {
        JavaClass clazz = Repository.lookupClass("com.rlax.corebin.tools.callgraph.TestBean");
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals("save")) {
                continue;
            }

            MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();
//            LineNumberTable lineNumberTable = methodGen.getLineNumberTable(constantPoolGen);
            LineNumberTable lineNumberTable = method.getLineNumberTable();

            for (InstructionHandle instructionHandle : instructionList) {
                short opCode = instructionHandle.getInstruction().getOpcode();
                // 如果是方法调用
                if (opCode >= Const.INVOKEVIRTUAL && opCode <= Const.INVOKEDYNAMIC) {
                    InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();

                    // 获取被调用类名
                    String calleeClassName = invokeInstruction.getReferenceType(constantPoolGen).toString();
                    // 获取被调用方法
                    String calleeMethodName = invokeInstruction.getMethodName(constantPoolGen);
                    // 获取被调用方法调用参数
                    Type[] argTypes = invokeInstruction.getArgumentTypes(constantPoolGen);

                    Console.log("{}.{}()", clazz.getClassName(), method.getName());
                    Console.log("[{}:{}] {}.{}({})", clazz.getClassName(), lineNumberTable.getSourceLine(instructionHandle.getPosition()), calleeClassName, calleeMethodName, argTypes);
                }
            }
        }
    }

    @Test
    void testTrace() throws ClassNotFoundException {
        logTrace("com.rlax.corebin.tools.callgraph.TestBean", "public void save(String ps1)");
    }

    @Test
    void methodTest() throws ClassNotFoundException {
        JavaClass clazz = Repository.lookupClass("com.rlax.corebin.tools.callgraph.TestBean");
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            final String access = Utility.accessToString(method.getAccessFlags());
            // Get name and signature from constant pool
            ConstantUtf8 c = method.getConstantPool().getConstantUtf8(method.getSignatureIndex());
            String signature = c.getBytes();
            c = method.getConstantPool().getConstantUtf8(method.getNameIndex());
            final String name = c.getBytes();
            signature = Utility.methodSignatureToString(signature, name, access, true, method.getLocalVariableTable());

            Console.log(signature);
        }

    }

    private void logTrace(String fullCallClassName, String callMethodName) throws ClassNotFoundException {
        JavaClass clazz = Repository.lookupClass(fullCallClassName);
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(clazz.getConstantPool());

        if (fullCallClassName.equals(CommonConstants.CLASS_NAME_OBJECT)) {
            return;
        }

        for (Method method : clazz.getMethods()) {
            if (!getMethodFullName(method).equals(callMethodName)) {
                continue;
            }

            MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();
            LineNumberTable lineNumberTable = method.getLineNumberTable();

            for (InstructionHandle instructionHandle : instructionList) {
                short opCode = instructionHandle.getInstruction().getOpcode();
                // 如果是方法调用
                if (opCode >= Const.INVOKEVIRTUAL && opCode <= Const.INVOKEDYNAMIC) {
                    InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();

                    // 获取被调用类名
                    String callerClassName = invokeInstruction.getClassName(constantPoolGen);
                    // 获取被调用方法
                    String callerMethodName = invokeInstruction.getMethodName(constantPoolGen);
                    // 获取被调用方法调用参数
                    Type[] argTypes = invokeInstruction.getArgumentTypes(constantPoolGen);

                    String callSignature = invokeInstruction.getSignature(constantPoolGen);

                    if (callerMethodName.equals(CommonConstants.METHOD_NAME_INIT)) {
                        return;
                    }

//                    Console.log(method.getSignature());
//                    Console.log(invokeInstruction.getSignature(constantPoolGen));
//                    Console.log(invokeInstruction.getClassName(constantPoolGen));
//                    Console.log(invokeInstruction.getReturnType(constantPoolGen));
//                    Console.log(invokeInstruction.getReferenceType(constantPoolGen));

                    Method callMethod = getMethod(callerClassName, callerMethodName, callSignature);

//                    Console.log("{}.{}()", clazz.getClassName(), method.getName());
                    Console.log("[{}:{}] {}.{}({})", clazz.getClassName(), lineNumberTable.getSourceLine(instructionHandle.getPosition()), callerClassName, callerMethodName, argTypes);

                    logTrace(callerClassName, getMethodFullName(callMethod));
                }
            }
        }
    }

    private Method getMethod(String callerClassName, String callerMethodName, String callSignature) throws ClassNotFoundException {
        JavaClass clazz = Repository.lookupClass(callerClassName);
        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals(callerMethodName)) {
                continue;
            }
            if (!method.getSignature().equals(callSignature)) {
//                Console.log(method.getSignature() + " " + callSignature);
                continue;
            }


            return method;
        }

        return getMethod(clazz.getSuperclassName(), callerMethodName, callSignature);
    }

    private String getMethodFullName(Method method) {
        final String access = Utility.accessToString(method.getAccessFlags());
        // Get name and signature from constant pool
        ConstantUtf8 c = method.getConstantPool().getConstantUtf8(method.getSignatureIndex());
        String signature = c.getBytes();
        c = method.getConstantPool().getConstantUtf8(method.getNameIndex());
        final String name = c.getBytes();
        return Utility.methodSignatureToString(signature, name, access, true, method.getLocalVariableTable());
    }

}
