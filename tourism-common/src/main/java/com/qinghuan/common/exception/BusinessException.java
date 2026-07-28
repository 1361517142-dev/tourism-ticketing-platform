package com.qinghuan.common.exception;

import java.util.Objects;

/**
 * 可预期的业务异常，由全局异常处理器转换为统一响应。
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
