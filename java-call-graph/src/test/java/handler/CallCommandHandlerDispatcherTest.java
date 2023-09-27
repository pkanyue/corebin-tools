package handler;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.rlax.corebin.tools.callgraph.call.*;
import com.rlax.corebin.tools.callgraph.handler.*;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/22
 */
public class CallCommandHandlerDispatcherTest {

    @Test
    void run() {
        CallCommandHandlerDispatcher callCommandHandlerDispatcher = new CallCommandHandlerDispatcher();
        callCommandHandlerDispatcher.addHandler(new InvokeJvmDynamicCallCommandHandler());
        callCommandHandlerDispatcher.addHandler(new InvokeJvmStaticCallCommandHandler());
        callCommandHandlerDispatcher.addHandler(new InvokeJvmSpecialCallCommandHandler());
        callCommandHandlerDispatcher.addHandler(new InvokeJvmVirtualCallCommandHandler());
        callCommandHandlerDispatcher.addHandler(new InvokeJvmInterfaceCallCommandHandler());

        String fullCallClassName = "bean.TestBean";
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);
        java.lang.reflect.Method m = ReflectUtil.getMethod(ClassUtil.loadClass(fullCallClassName), "callable", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);

        ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
        MethodGen methodGen = new MethodGen(method, callerJavaClass.getClassName(), constantPoolGen);
        InstructionList instructionList = methodGen.getInstructionList();

        for (InstructionHandle instructionHandle : instructionList) {
            if (instructionHandle.getInstruction() instanceof InvokeInstruction) {
                MethodCallInfo methodCallInfo = callCommandHandlerDispatcher.doDispatch(callerJavaClass, method, instructionHandle, "1");
                Console.log(methodCallInfo);
            }
        }
    }


}
