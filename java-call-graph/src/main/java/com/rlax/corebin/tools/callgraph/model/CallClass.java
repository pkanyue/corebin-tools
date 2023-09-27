package com.rlax.corebin.tools.callgraph.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.bcel.classfile.JavaClass;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用者类，发起调用的类信息
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
public class CallClass {

    /**
     * key: fullClassName
     * value: CallClass
     */
    private static final Map<String, CallClass> CALL_CLASS_POOL = new ConcurrentHashMap<>();

    /**
     * 全类名，如：com.rlax.corebin.tools.callgraph.TestBean
     */
    private String fullClassName;
    private String className;
    private String packageName;

    /**
     * 构建 CallerClass
     * @param javaClass 全类名
     * @return CallerClass
     */
    public static CallClass create(JavaClass javaClass) {
        String fullClassName = javaClass.getClassName();
        if (CALL_CLASS_POOL.containsKey(fullClassName)) {
            return CALL_CLASS_POOL.get(fullClassName);
        }

        String className = null;
        final List<String> packages = StrUtil.split(fullClassName, CharUtil.DOT);
        if (ArrayUtil.isNotEmpty(packages)) {
            className = packages.get(packages.size() - 1);
        }

        CallClass callClass =  CallClass.builder()
                .fullClassName(fullClassName)
                .packageName(javaClass.getPackageName())
                .className(className)
                .build();

        CALL_CLASS_POOL.put(fullClassName, callClass);
        return callClass;
    }
}
