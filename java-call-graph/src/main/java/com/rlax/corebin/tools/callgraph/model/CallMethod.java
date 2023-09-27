package com.rlax.corebin.tools.callgraph.model;

import com.rlax.corebin.core.util.StringPool;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.bcel.classfile.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用者，所在的方法
 *
 * @author Rlax
 * @date 2023/09/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class CallMethod {

    /**
     * key: ownerClass.getFullClassName() + StringPool.DOT + fullMethodString
     * value: CallMethod
     */
    private static final Map<String, CallMethod> METHOD_POOL = new ConcurrentHashMap<>();

    /**
     * 所属类
     */
    private CallClass ownerClass;

    private String methodName;
    /**
     * 全方法字符串，如：public void save(String ps1)
     */
    private String fullMethodString;

    public static CallMethod create(CallClass ownerClass, Method method) {
        String fullMethodString = BaceUtil.getMethodFullName(method);
        String key = ownerClass.getFullClassName() + StringPool.DOT + fullMethodString;
        if (METHOD_POOL.containsKey(key)) {
            return METHOD_POOL.get(key);
        }

        CallMethod callMethod = CallMethod.builder()
                .ownerClass(ownerClass)
                .methodName(method.getName())
                .fullMethodString(fullMethodString)
                .build();

        METHOD_POOL.put(key, callMethod);
        return callMethod;
    }
}
