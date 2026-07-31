package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.TicketTypeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketType extends BaseEntity {

    private Long venueId;
    private String name;
    private String description;
    private String audienceRule;
    private BigDecimal basePrice;
    private TicketTypeStatus status = TicketTypeStatus.ENABLED;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
