package com.qinghuan.common.response;

import com.qinghuan.common.exception.ErrorCode;

import java.util.Objects;

/**
 * 所有 HTTP 接口统一使用的响应结构。
 *
 * @param code    机器可读的结果码
 * @param message 面向调用方的简要信息
 * @param data    业务数据，失败时通常为空
 */
public record ApiResponse<T>(String code, String message, T data) {

    public ApiResponse {
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return failure(errorCode, errorCode.getMessage());
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        return new ApiResponse<>(errorCode.getCode(), message, null);
    }
}
