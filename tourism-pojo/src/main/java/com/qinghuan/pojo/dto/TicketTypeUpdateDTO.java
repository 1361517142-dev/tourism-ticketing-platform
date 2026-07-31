package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.TicketTypeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 运营者修改票种资料时提交的字段。
 */
@Getter
@Setter
public class TicketTypeUpdateDTO {

    @NotBlank(message = "票种名称不能为空")
    @Size(max = 100, message = "票种名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "票种描述不能超过500个字符")
    private String description;

    @Size(max = 500, message = "适用规则不能超过500个字符")
    private String audienceRule;

    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.00", message = "基础价格不能小于0")
    @Digits(integer = 8, fraction = 2, message = "基础价格最多为8位整数和2位小数")
    private BigDecimal basePrice;

    @NotNull(message = "票种状态不能为空")
    private TicketTypeStatus status;
}
