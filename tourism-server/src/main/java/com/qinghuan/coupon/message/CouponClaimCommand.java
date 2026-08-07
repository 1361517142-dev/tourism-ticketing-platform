package com.qinghuan.coupon.message;

import java.time.LocalDateTime;

/**
 * 游客取得 Redis 抢券资格后发送到 Kafka 的命令消息。
 *
 * 消费者根据 requestId 保证消息幂等，
 * 根据 activityId + userId 保证同一活动一人一券。
 */
public record CouponClaimCommand(
        String requestId,
        Long activityId,
        Long userId,
        LocalDateTime requestedAt,
        int schemaVersion) {

    /** 当前消息结构版本。 */
    public static final int CURRENT_SCHEMA_VERSION = 1;

    /**
     * 创建当前版本的抢券命令，避免调用方到处手写版本号。
     */
    public static CouponClaimCommand create(
            String requestId,
            Long activityId,
            Long userId,
            LocalDateTime requestedAt) {
        return new CouponClaimCommand(
                requestId,
                activityId,
                userId,
                requestedAt,
                CURRENT_SCHEMA_VERSION
        );
    }
}