package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Ticket extends BaseEntity {

    private String ticketCode;
    private Long orderItemId;
    private TicketStatus status;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime verifiedAt;
}
