package com.rlax.corebin.tools.callgraph;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.rlax.corebin.tools.callgraph.call.MethodCallInfo;
import com.rlax.corebin.tools.callgraph.common.CommonConstants;
import com.rlax.corebin.tools.callgraph.exception.CallGraphGenerateException;
import com.rlax.corebin.tools.callgraph.handler.*;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CallGraphGenerator
 *
 * @author Rlax
 * @date 2023/09/27
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CallGraphGenerator {

    @Getter
    private final List<MethodCallInfo> callInfoList = new ArrayList<>();

    private CallCommandHandlerDispatcher callCommandHandlerDispatcher;
    private ExecutorService executor;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public void init() {
        if (initialized.compareAndSet(false, true)) {
            callCommandHandlerDispatcher = new CallCommandHandlerDispatcher();
            callCommandHandlerDispatcher.addHandler(new InvokeJvmDynamicCallCommandHandler());
            callCommandHandlerDispatcher.addHandler(new InvokeJvmStaticCallCommandHandler());
            callCommandHandlerDispatcher.addHandler(new InvokeJvmSpecialCallCommandHandler());
            callCommandHandlerDispatcher.addHandler(new InvokeJvmVirtualCallCommandHandler());
            callCommandHandlerDispatcher.addHandler(new InvokeJvmInterfaceCallCommandHandler());

            executor = ExecutorBuilder.create()
                    .setCorePoolSize(2)
                    .setMaxPoolSize(2)
                    .setKeepAliveTime(0)
                    .setThreadFactory(ThreadUtil.newNamedThreadFactory("call-executor-", true))
                    .buildFinalizable();
            return;
        }
        throw new CallGraphGenerateException(3, "this component has initialized");
    }

    public void submitMethodCall(MethodCallInfo methodCallInfo) {
        if (!initialized.get()) {
            throw new CallGraphGenerateException(2, "CallGraphGenerator not initialize");
        }

        if (methodCallInfo.getCalledMethodName().equals(CommonConstants.METHOD_NAME_INIT)) {
            log.info("ignore method : {}", methodCallInfo.getCalledMethodName());
            return;
        }

        log.info("submit method call : {}", methodCallInfo);
        callInfoList.add(methodCallInfo);
        JavaClass callerJavaClass = BaceUtil.loadClass(methodCallInfo.getCalledClassName());
        Method method = BaceUtil.findMethod(callerJavaClass, methodCallInfo.getCalledMethodName(), methodCallInfo.getCalledMethodArguments());
        executor.submit(() -> {
            ConstantPoolGen constantPoolGen = new ConstantPoolGen(callerJavaClass.getConstantPool());
            MethodGen methodGen = new MethodGen(method, callerJavaClass.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();

            int i = 1;
            for (InstructionHandle instructionHandle : instructionList) {
                if (instructionHandle.getInstruction() instanceof InvokeInstruction) {
                    String callOrder = StrUtil.isBlank(methodCallInfo.getCallOrder()) ? String.valueOf(i) : StrUtil.format("{}.{}", methodCallInfo.getCallOrder(), i++);
                    MethodCallInfo callInfo = callCommandHandlerDispatcher.doDispatch(callerJavaClass, method, instructionHandle, callOrder);
                    if (callInfo != null) {
                        submitMethodCall(callInfo);
                    }
                }
            }
        });
    }


}
