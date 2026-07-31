package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 场次中的单个票种售价和配额配置。
 */
@Getter
@Setter
public class SessionTicketTypeConfigDTO {

    @NotNull(message = "票种ID不能为空")
    @Positive(message = "票种ID必须为正数")
    private Long ticketTypeId;

    @NotNull(message = "场次售价不能为空")
    @DecimalMin(value = "0.00", message = "场次售价不能小于0")
    @Digits(integer = 8, fraction = 2, message = "场次售价最多为8位整数和2位小数")
    private BigDecimal salePrice;

    @NotNull(message = "票种分配数量不能为空")
    @Positive(message = "票种分配数量必须大于0")
    private Integer allocatedQuantity;
}
