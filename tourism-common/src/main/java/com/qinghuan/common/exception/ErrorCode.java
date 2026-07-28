package com.qinghuan.common.exception;

/**
 * 跨业务域通用错误码。业务模块可以在此基础上继续扩展自己的错误语义。
 */
public enum ErrorCode {

    SUCCESS("SUCCESS", "操作成功"),
    INVALID_REQUEST("INVALID_REQUEST", "请求参数不合法"),
    UNAUTHORIZED("UNAUTHORIZED", "请先登录"),
    FORBIDDEN("FORBIDDEN", "无权执行该操作"),
    NOT_FOUND("NOT_FOUND", "请求的资源不存在"),
    CONFLICT("CONFLICT", "当前状态不允许执行该操作"),
    INTERNAL_ERROR("INTERNAL_ERROR", "系统繁忙，请稍后重试");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
