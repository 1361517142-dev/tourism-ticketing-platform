package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 单张订单明细的选择信息。
 * sessionTicketTypeId 指向场次票种配置，而不是普通票种 ID。
 */
public record OrderCreateItemRequest(
        @NotNull @Positive Long visitorId,
        @NotNull @Positive Long sessionTicketTypeId) {
}
