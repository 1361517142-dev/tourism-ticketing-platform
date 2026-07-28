package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.AdmissionSessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class AdmissionSession extends BaseEntity {

    private Long venueId;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime bookingStartAt;
    private LocalDateTime bookingEndAt;
    private Integer totalCapacity;
    private Integer remainingCapacity;
    private AdmissionSessionStatus status;
}
