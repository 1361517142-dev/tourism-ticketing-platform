package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SessionTicketType extends BaseEntity {

    private Long sessionId;
    private Long ticketTypeId;
    private BigDecimal salePrice;
    private SaleStatus status;
}
