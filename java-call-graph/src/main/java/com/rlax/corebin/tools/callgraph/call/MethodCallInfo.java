package com.rlax.corebin.tools.callgraph.call;

import com.rlax.corebin.tools.callgraph.common.CallType;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.bcel.generic.Type;

/**
 * @author Rlax
 * @date 2023/09/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class MethodCallInfo {

    // 调用者信息

    private String callerClassName;
    private String callerMethodName;
    private Type[] callerMethodArguments;
    private Type callerMethodReturnType;

    /** 调用者调用被调用者方法源码行数 */
    private int callLineNum;
    // 被调用者信息

    private String calledClassName;
    private String calledMethodName;
    private Type[] calledMethodArguments;
    private Type calledMethodReturnType;

    /**
     * JVM 调用指令
     */
    short jvmOpCode;
    /**
     * 真实调用类型
     */
    CallType callType;

}
