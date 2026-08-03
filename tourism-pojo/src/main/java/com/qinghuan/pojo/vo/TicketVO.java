package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** 游客端电子票列表和详情信息。 */
@Getter
@Setter
public class TicketVO {

    private Long id;
    private String ticketCode;
    private TicketStatus status;
    private String venueName;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String visitorName;
    private String ticketTypeName;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime verifiedAt;

    // 以下字段只在详情接口中返回。
    private Long orderId;
    private String orderNo;
    private Long venueId;
    private String venueAddress;
    private Long visitorId;
}
