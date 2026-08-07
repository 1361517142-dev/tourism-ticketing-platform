package com.qinghuan.pojo.enums;

/**
 * 抢券请求失败原因。
 */
public enum CouponClaimFailureReason {

    /** Lua 已经预扣，但消息最终未能发送到 Kafka。 */
    MESSAGE_SEND_FAILED,

    /** MySQL 最终库存不足。 */
    SOLD_OUT,

    /** 活动已经取消、结束或请求时间不合法。 */
    ACTIVITY_UNAVAILABLE
}