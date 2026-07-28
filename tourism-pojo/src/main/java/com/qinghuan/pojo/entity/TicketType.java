package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.TicketTypeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TicketType extends BaseEntity {

    private Long venueId;
    private String name;
    private String description;
    private String audienceRule;
    private BigDecimal basePrice;
    private TicketTypeStatus status;
}
