package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.UserCouponStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** 查询当前游客优惠券的可选筛选条件。 */
@Getter
@Setter
public class UserCouponQueryDTO {
    @Positive(message = "景点ID必须为正数")
    private Long venueId;
    private UserCouponStatus status;
    @DecimalMin(value = "0.00", message = "订单金额不能小于0")
    @Digits(integer = 8, fraction = 2, message = "订单金额最多保留两位小数")
    private BigDecimal orderAmount;
}
