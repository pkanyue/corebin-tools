package handler;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.rlax.corebin.tools.callgraph.handler.InvokeJvmDynamicCallCommandHandler;
import com.rlax.corebin.tools.callgraph.call.MethodCallInfo;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/22
 */
public class InvokeJvmDynamicCallCommandHandlerTest {

    @Test
    void run() {
        InvokeJvmDynamicCallCommandHandler handler = new InvokeJvmDynamicCallCommandHandler();

        String fullCallClassName = "bean.TestBean";
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, callerJavaClass.getClassName(), constantPoolGen);
        InstructionList instructionList = methodGen.getInstructionList();

        for (InstructionHandle instructionHandle : instructionList) {
            if (instructionHandle.getInstruction() instanceof InvokeInstruction) {
                MethodCallInfo methodCallInfo = handler.handle(callerJavaClass, method, instructionHandle, "1");
                Console.log(methodCallInfo);
            }
        }
    }


}
