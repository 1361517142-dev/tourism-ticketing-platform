package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.UserCouponStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 当前游客持有的优惠券信息。 */
@Getter
@Setter
public class UserCouponVO {
    private Long id;
    private Long activityId;
    private Long venueId;
    private String venueName;
    private String couponName;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private UserCouponStatus status;
    private LocalDateTime acquiredAt;
}
