package com.rlax.corebin.tools.callgraph.util;

import com.rlax.corebin.tools.callgraph.exception.CallGraphGenerateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.Type;

import java.util.Arrays;

/**
 * BaceUtil
 *
 * @author Rlax
 * @date 2023/09/13
 */
@Slf4j
public class BaceUtil {

    public static JavaClass loadClass(String fullCallClassName) {
        try {
            return Repository.lookupClass(fullCallClassName);
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException: ", e);
            throw new CallGraphGenerateException(e);
        }
    }

    public static String getMethodFullName(Method method) {
        final String access = Utility.accessToString(method.getAccessFlags());
        // Get name and signature from constant pool
        ConstantUtf8 c = method.getConstantPool().getConstantUtf8(method.getSignatureIndex());
        String signature = c.getBytes();
        c = method.getConstantPool().getConstantUtf8(method.getNameIndex());
        final String name = c.getBytes();
        return Utility.methodSignatureToString(signature, name, access, true, method.getLocalVariableTable());
    }

    public static Method findMethodByFullName(JavaClass javaClass, String fullMethodString) {
        for (Method method : javaClass.getMethods()) {
            if (getMethodFullName(method).equals(fullMethodString)) {
                return method;
            }
        }

        return null;
    }

    @SneakyThrows
    public static Method findMethod(JavaClass javaClass, String callerMethodName, Type[] calledArguments) {
        for (Method method : javaClass.getMethods()) {
            if (!method.getName().equals(callerMethodName)) {
                continue;
            }
            if (!Arrays.equals(method.getArgumentTypes(), calledArguments)) {
                continue;
            }

            return method;
        }

        // 找不到的话，会去父类找
        return findMethod(javaClass.getSuperClass(), callerMethodName, calledArguments);
    }

    public static Method findMethodByReflectMethod(JavaClass javaClass, java.lang.reflect.Method method) {
        return javaClass.getMethod(method);
    }
}
