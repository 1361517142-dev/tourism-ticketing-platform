package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/** 核销结果中返回的票券概要。 */
@Getter
@Setter
public class VerificationTicketVO {

    private Long id;
    private String ticketCode;
    private TicketStatus status;
    private String venueName;
    private String visitorName;
    private String ticketTypeName;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
