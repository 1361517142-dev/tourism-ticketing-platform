package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 场次中的票种展示信息。
 */
@Getter
@Setter
public class SessionTicketTypeVO {

    private Long ticketTypeId;
    private String ticketTypeName;
    private BigDecimal salePrice;
    private Integer allocatedQuantity;
    private Integer remainingQuantity;
    private SaleStatus status;
}
