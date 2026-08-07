package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.BookingOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** 订单分页列表使用的摘要信息。 */
@Getter
@Setter
public class OrderSummaryVO {

    private Long id;
    private String orderNo;
    private String venueName;
    private Long sessionId;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer quantity;
    private Long userCouponId;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BookingOrderStatus status;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
}
