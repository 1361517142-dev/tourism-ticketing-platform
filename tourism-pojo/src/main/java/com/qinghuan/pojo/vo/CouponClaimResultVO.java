package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.CouponClaimStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 游客轮询异步抢券任务时返回的数据库结果。 */
@Getter
@Setter
public class CouponClaimResultVO {
    private String requestId;
    private Long activityId;
    private CouponClaimStatus status;
    private Long userCouponId;
    private String failureReason;
    private LocalDateTime processedAt;
}
