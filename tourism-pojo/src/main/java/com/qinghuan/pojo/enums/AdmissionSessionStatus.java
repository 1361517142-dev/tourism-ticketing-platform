package com.qinghuan.pojo.enums;

public enum AdmissionSessionStatus {
    DRAFT,
    OPEN,
    CLOSED,
    ENDED,
    CANCELLED;

    /**
     * 根据当前状态和业务事件计算下一状态，未声明的组合均为非法流转。
     */
    public AdmissionSessionStatus next(SessionEvent event) {
        return switch (this) {
            case DRAFT -> switch (event) {
                case PUBLISH -> OPEN;
                case CANCEL -> CANCELLED;
                default -> throw new IllegalStateException("草稿场次不支持该事件");
            };
            case OPEN -> switch (event) {
                case CLOSE_BOOKING -> CLOSED;
                case CANCEL -> CANCELLED;
                case SESSION_ENDED -> ENDED;
                default -> throw new IllegalStateException("开放场次不支持该事件");
            };
            case CLOSED -> switch (event) {
                case REOPEN_BOOKING -> OPEN;
                case CANCEL -> CANCELLED;
                case SESSION_ENDED -> ENDED;
                default -> throw new IllegalStateException("关闭场次不支持该事件");
            };
            case ENDED, CANCELLED ->
                    throw new IllegalStateException("终态场次不能继续变更");
        };
    }
}
