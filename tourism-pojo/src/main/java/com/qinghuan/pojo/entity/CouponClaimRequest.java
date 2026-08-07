package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.CouponClaimStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Kafka 消费后写入数据库的异步抢券结果。 */
@Getter
@Setter
public class CouponClaimRequest {
    private String requestId;
    private Long activityId;
    private Long userId;
    private CouponClaimStatus status;
    private Long userCouponId;
    private String failureReason;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
