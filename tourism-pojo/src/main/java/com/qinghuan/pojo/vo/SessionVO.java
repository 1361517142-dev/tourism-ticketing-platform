package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.entity.AdmissionSession;
import com.qinghuan.pojo.enums.AdmissionSessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 运营端场次详情和列表项。
 */
@Getter
@Setter
public class SessionVO {

    private Long id;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime bookingStartAt;
    private LocalDateTime bookingEndAt;
    private Integer totalCapacity;
    private Integer remainingCapacity;
    private AdmissionSessionStatus status;
    private List<SessionTicketTypeVO> ticketTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SessionVO from(
            AdmissionSession session, List<SessionTicketTypeVO> ticketTypes) {
        SessionVO vo = new SessionVO();
        vo.setId(session.getId());
        vo.setVisitDate(session.getVisitDate());
        vo.setStartTime(session.getStartTime());
        vo.setEndTime(session.getEndTime());
        vo.setBookingStartAt(session.getBookingStartAt());
        vo.setBookingEndAt(session.getBookingEndAt());
        vo.setTotalCapacity(session.getTotalCapacity());
        vo.setRemainingCapacity(session.getRemainingCapacity());
        vo.setStatus(session.getStatus());
        vo.setTicketTypes(ticketTypes);
        vo.setCreatedAt(session.getCreatedAt());
        vo.setUpdatedAt(session.getUpdatedAt());
        return vo;
    }
}
