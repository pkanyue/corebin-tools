package com.rlax.corebin.tools.callgraph.common;

import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * 常量
 *
 * @author Rlax
 * @date 2023/09/08
 */
public interface CommonConstants {

    String CLASS_NAME_RUNNABLE = Runnable.class.getName();
    String CLASS_NAME_CALLABLE = Callable.class.getName();
    String CLASS_NAME_THREAD = Thread.class.getName();
    String CLASS_NAME_TIMER_TASK = TimerTask.class.getName();
    String CLASS_NAME_OBJECT = Object.class.getName();
    String CLASS_NAME_STRING = String.class.getName();
    String CLASS_NAME_CLASS = Class.class.getName();
    String CLASS_NAME_NULL_POINTER_EXCEPTION = NullPointerException.class.getName();

    String CLASS_NAME_TRANSACTION_CALLBACK = "org.springframework.transaction.support.TransactionCallback";
    String CLASS_NAME_TRANSACTION_CALLBACK_WITHOUT_RESULT = "org.springframework.transaction.support.TransactionCallbackWithoutResult";

    String SIMPLE_CLASS_NAME_OBJECT = Object.class.getSimpleName();

    String METHOD_NAME_INIT = "<init>";
    String METHOD_NAME_START = "start";

    String METHOD_DO_IN_TRANSACTION = "doInTransaction";
    String METHOD_DO_IN_TRANSACTION_WITHOUT_RESULT = "doInTransactionWithoutResult";

    String METHOD_RUNNABLE_RUN = "run";
    String METHOD_CALLABLE_CALL = "call";
    
}
