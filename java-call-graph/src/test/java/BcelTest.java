import cn.hutool.core.lang.Console;
import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/06
 */
public class BcelTest {

    @Test
    void classTest() throws ClassNotFoundException {
//        JavaClass javaClass = new ClassParser(inputStream, jarEntryName).parse();

        JavaClass clazz = Repository.lookupClass("com.rlax.corebin.tools.callgraph.TestBean");
        Console.log("======== 类信息");
        Console.log(clazz);

        ConstantPool constantPool = clazz.getConstantPool();
        Console.log("======== 常量池");
        Console.log(clazz.getConstantPool());

        Console.log("======== 引用类");
        for (Constant constant : constantPool.getConstantPool()) {
            if (constant != null && constant.getTag() == Const.CONSTANT_Class) {
                Console.log("{}: {}", constant.getTag(), constantPool.constantToString(constant));
            }
        }

        Console.log("======== 方法调用");
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(constantPool);

        for (Method method : clazz.getMethods()) {
            MethodGen methodGen = new MethodGen(method, clazz.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();

            Console.log("======== " + method.getName());

            for (InstructionHandle instructionHandle : instructionList) {
                short opCode = instructionHandle.getInstruction().getOpcode();
                if (opCode >= Const.INVOKEVIRTUAL && opCode <= Const.INVOKEDYNAMIC) {
                    // 说明是方法调用
                    Console.log("======== 调用方法 {} {}", opCode, instructionHandle);

                    InvokeInstruction invokeInstruction = (InvokeInstruction) instructionHandle.getInstruction();

                    // 获取调用方法
                    String callerMethodName = methodGen.getName();
                    // 获取被调用类名
                    String calleeClassName = invokeInstruction.getReferenceType(constantPoolGen).toString();
                    // 获取被调用方法
                    String calleeMethodName = invokeInstruction.getMethodName(constantPoolGen);
                    // 获取被调用方法调用参数
                    Type[] argTypes = invokeInstruction.getArgumentTypes(constantPoolGen);

                    Console.log("======== 调用方法 {} {} {} {} {} {}", opCode, instructionHandle, callerMethodName, calleeClassName, calleeMethodName, argTypes);
                } else {
                    Console.log("======== 其他 {} {}", opCode, instructionHandle);
                }
            }

            Console.log("======== instructionList " + instructionList);
        }

    }

}
