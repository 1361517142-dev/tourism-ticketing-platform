package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.BookingOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedVO(Long id, String orderNo, BigDecimal totalAmount, BookingOrderStatus status, LocalDateTime expireAt) {
}
