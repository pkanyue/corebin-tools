package com.rlax.corebin.tools.callgraph.common;

import com.rlax.corebin.core.base.BaseEnum;
import org.apache.bcel.Const;

/**
 * 方法调用类型枚举
 * @author Rlax
 *
 */
public enum CallType implements BaseEnum {
    // 原始的调用类型，不以“_”开头，其他的都需要以“_”开头
    JVM_INVOKE_VIRTUAL(String.valueOf(Const.INVOKEVIRTUAL), "INVOKEVIRTUAL"),
    JVM_INVOKE_INTERFACE(String.valueOf(Const.INVOKEINTERFACE), "INVOKEINTERFACE"),
    JVM_INVOKE_SPECIAL(String.valueOf(Const.INVOKESPECIAL), "INVOKESPECIAL"),
    JVM_INVOKE_STATIC(String.valueOf(Const.INVOKESTATIC), "INVOKESTATIC"),
    JVM_INVOKE_DYNAMIC(String.valueOf(Const.INVOKEDYNAMIC), "INVOKEDYNAMIC"),
    // Spring Bean相关的调用类型
    SPRING_BEAN_ACTUAL_INTERFACE("_SPR_ACT_I", "被调用接口为 Spring Bean ，替换为实际的类型"),
    SPRING_BEAN_ACTUAL_CLASS("_SPR_ACT_C", "被调用类为 Spring Bean ，替换为实际的类型"),
    // 其他调用类型
    ACTUAL_INTERFACE("_ACT_I", "被调用接口替换为实际的类型"),
    ACTUAL_CLASS("_ACT_C", "被调用类替换为实际的类型"),
    INTERFACE_CALL_IMPL_CLASS("_ITF", "接口调用实现类"),
    LAMBDA("_LM", "Lambda表达式"),
    RUNNABLE_INIT_RUN1("_RIR1", "其他方法调用Runnable 构造函数"),
    RUNNABLE_INIT_RUN2("_RIR2", "Runnable 构造函数调用 run() 方法"),
    CALLABLE_INIT_CALL1("_CIC1", "其他方法调用Callable 构造函数"),
    CALLABLE_INIT_CALL2("_CIC2", "Callable 构造函数调用 call() 方法"),
    TX_CALLBACK_INIT_CALL1("_TCID1", "其他方法调用TransactionCallback 构造函数"),
    TX_CALLBACK_INIT_CALL2("_TCID2", "TransactionCallback 构造函数调用 doInTransaction() 方法"),
    TX_CALLBACK_WR_INIT_CALL1("_TCWRID1", "其他方法调用TransactionCallbackWithoutResult 构造函数"),
    TX_CALLBACK_WR_INIT_CALL2("_TCWRID2", "TransactionCallbackWithoutResult 构造函数调用 doInTransactionWithoutResult() 方法"),
    THREAD_START_RUN("_TSR", "Thread start() 方法调用 run() 方法"),
    SUPER_CALL_CHILD("_SCC", "父类调用子类方法"),
    CHILD_CALL_SUPER("_CCS", "子类调用父类方法"),
    CHILD_CALL_SUPER_SPECIAL("_CCS_SPE", "子类通过super.调用父类方法"),
    CHILD_CALL_SUPER_INTERFACE("_CCS_I", "子接口调用父接口方法"),
    MANUAL_ADDED("_MA", "人工添加的方法调用"),
    METHOD_ANNOTATION_ADDED("_MAA", "通过方法注解添加的调用关系"),
    ILLEGAL("ILLEGAL", "ILLEGAL"),
    ;

    private final String code;

    private final String desc;

    CallType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static CallType getByCode(String code) {
        for (CallType callType : CallType.values()) {
            if (callType.getCode().equals(code)) {
                return callType;
            }
        }

        return null;
    }

}
