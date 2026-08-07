package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.UserCouponStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 游客实际持有的优惠券规则快照。 */
@Getter
@Setter
public class UserCoupon extends BaseEntity {
    private Long activityId;
    private Long userId;
    private Long venueId;
    /** 以下规则字段均为领取时快照，不随活动后续状态变化。 */
    private String couponName;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    /** AVAILABLE/LOCKED/USED/EXPIRED，对应订单生命周期。 */
    private UserCouponStatus status;
    private LocalDateTime acquiredAt;
    private LocalDateTime usedAt;
}
