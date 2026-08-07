package com.qinghuan.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 创建订单请求。
 * items 中的每一项代表“一个参观人购买一张场次票”。
 */
public record OrderCreateDTO(
        @NotNull @Positive Long sessionId,
        @NotEmpty @Valid List<OrderCreateItemRequest> items,
        // 可选；传入时由后端校验归属、景点、有效期和使用门槛。
        @Positive Long userCouponId) {

}
