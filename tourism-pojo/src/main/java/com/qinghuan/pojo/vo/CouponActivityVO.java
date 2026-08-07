package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.CouponActivityStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 运营端优惠券活动列表和详情。 */
@Getter
@Setter
public class CouponActivityVO {
    private Long id;
    private Long venueId;
    private String name;
    private BigDecimal thresholdAmount;
    private BigDecimal discountAmount;
    private Integer totalStock;
    private Integer remainingStock;
    private LocalDateTime claimStartAt;
    private LocalDateTime claimEndAt;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private CouponActivityStatus status;
    private Boolean cacheReady;
    private LocalDateTime preheatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
