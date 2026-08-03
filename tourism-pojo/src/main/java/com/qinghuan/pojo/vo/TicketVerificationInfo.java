package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Ticket Service 提供给核销模块的票券校验信息。 */
@Getter
@Setter
public class TicketVerificationInfo {

    private Long id;
    private String ticketCode;
    private TicketStatus status;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime verifiedAt;
    private String venueName;
    private String visitorName;
    private String ticketTypeName;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
