package com.rlax.corebin.tools.callgraph.exception;

import com.rlax.corebin.core.exception.BaseUncheckedException;

/**
 * @author Rlax
 * @date 2023/09/13
 */
public class CallGraphGenerateException extends BaseUncheckedException {

    public CallGraphGenerateException(Throwable cause) {
        super(cause);
    }

    public CallGraphGenerateException(int code, String message) {
        super(code, message);
    }

    public CallGraphGenerateException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public CallGraphGenerateException(int code, String format, Object... args) {
        super(code, format, args);
    }
}
