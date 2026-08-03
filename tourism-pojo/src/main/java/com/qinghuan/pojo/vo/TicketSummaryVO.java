package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 订单详情中附带的电子票概要。 */
@Getter
@Setter
public class TicketSummaryVO {

    private Long id;
    private String ticketCode;
    private TicketStatus status;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime verifiedAt;
}
