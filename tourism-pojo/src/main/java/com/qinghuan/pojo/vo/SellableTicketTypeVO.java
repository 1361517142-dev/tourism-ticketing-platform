package com.qinghuan.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** 游客创建订单时可选择的场次票种。 */
@Getter
@Setter
public class SellableTicketTypeVO {

    /** 创建订单时提交该 ID，而不是基础票种 ID。 */
    private Long sessionTicketTypeId;
    private String ticketTypeName;
    private String description;
    private String audienceRule;
    private BigDecimal salePrice;
    private Integer remainingQuantity;
}
