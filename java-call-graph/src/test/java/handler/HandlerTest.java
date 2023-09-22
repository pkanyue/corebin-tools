package handler;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.rlax.corebin.tools.callgraph.call.*;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/22
 */
public class HandlerTest {

    @Test
    void run() {
        InvokeJvmDynamicCallCommandHandler invokeJvmDynamicCallCommandHandler = new InvokeJvmDynamicCallCommandHandler();

        InvokeJvmStaticCallCommandHandler invokeJvmStaticCallCommandHandler = new InvokeJvmStaticCallCommandHandler();
        InvokeJvmSpecialCallCommandHandler invokeJvmSpecialCallCommandHandler = new InvokeJvmSpecialCallCommandHandler();
        InvokeJvmVirtualCallCommandHandler invokeJvmVirtualCallCommandHandler = new InvokeJvmVirtualCallCommandHandler();
        InvokeJvmInterfaceCallCommandHandler invokeJvmInterfaceCallCommandHandler = new InvokeJvmInterfaceCallCommandHandler();

        invokeJvmDynamicCallCommandHandler.setNextCallCommandHandler(invokeJvmStaticCallCommandHandler);
        invokeJvmStaticCallCommandHandler.setNextCallCommandHandler(invokeJvmSpecialCallCommandHandler);
        invokeJvmSpecialCallCommandHandler.setNextCallCommandHandler(invokeJvmVirtualCallCommandHandler);
        invokeJvmVirtualCallCommandHandler.setNextCallCommandHandler(invokeJvmInterfaceCallCommandHandler);



        String fullCallClassName = "bean.TestBean";
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, callerJavaClass.getClassName(), constantPoolGen);
        InstructionList instructionList = methodGen.getInstructionList();

        for (InstructionHandle instructionHandle : instructionList) {
            if (instructionHandle.getInstruction() instanceof InvokeInstruction) {
                MethodCallInfo methodCallInfo = invokeJvmDynamicCallCommandHandler.handle(callerJavaClass, method, instructionHandle);
                Console.log(methodCallInfo);
            }
        }
    }


}
