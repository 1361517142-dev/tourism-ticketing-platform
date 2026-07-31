package com.qinghuan.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 新建或修改草稿场次的请求参数。
 */
@Getter
@Setter
public class SessionWriteDTO {

    @NotNull(message = "参观日期不能为空")
    private LocalDate visitDate;

    @NotNull(message = "场次开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "场次结束时间不能为空")
    private LocalTime endTime;

    @NotNull(message = "预约开始时间不能为空")
    private LocalDateTime bookingStartAt;

    @NotNull(message = "预约结束时间不能为空")
    private LocalDateTime bookingEndAt;

    @NotNull(message = "场次容量不能为空")
    @Positive(message = "场次容量必须大于0")
    private Integer totalCapacity;

    @Valid
    @NotEmpty(message = "至少需要配置一个票种")
    private List<SessionTicketTypeConfigDTO> ticketTypes;
}
