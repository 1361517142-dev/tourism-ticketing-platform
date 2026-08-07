package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.CouponClaimDisplayState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 公开目录展示的可领取优惠券活动。 */
@Getter
@Setter
public class CatalogCouponActivityVO {
    private Long id;
    private Long venueId;
    private String name;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private LocalDateTime claimStartAt;
    private LocalDateTime claimEndAt;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private CouponClaimDisplayState claimState;
}
