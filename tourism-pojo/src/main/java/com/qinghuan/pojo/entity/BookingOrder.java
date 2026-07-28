package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.BookingOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingOrder extends BaseEntity {

    private String orderNo;
    private Long userId;
    private Long sessionId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BookingOrderStatus status;
    private String paymentNo;
    private LocalDateTime expireAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime closedAt;
    private LocalDateTime completedAt;
    private LocalDateTime refundAt;
}
