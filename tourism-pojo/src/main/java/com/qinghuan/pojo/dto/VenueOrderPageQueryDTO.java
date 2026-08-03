package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/** 运营端订单分页条件，查询范围始终限制在当前账号所属景点。 */
@Getter
@Setter
public class VenueOrderPageQueryDTO extends OrderPageQueryDTO {

    @Size(max = 32, message = "订单号长度不能超过32个字符")
    private String orderNo;

    @Positive(message = "场次ID必须为正数")
    private Long sessionId;

    private LocalDate visitDate;
}
