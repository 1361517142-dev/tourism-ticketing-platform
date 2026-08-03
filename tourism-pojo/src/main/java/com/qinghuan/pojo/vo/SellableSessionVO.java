package com.qinghuan.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** 游客端指定日期下仍可预约的场次。 */
@Getter
@Setter
public class SellableSessionVO {

    private Long id;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime bookingEndAt;
    private Integer remainingCapacity;
    private List<SellableTicketTypeVO> ticketTypes;
}
