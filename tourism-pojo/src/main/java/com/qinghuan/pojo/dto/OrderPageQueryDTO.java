package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.BookingOrderStatus;
import lombok.Getter;
import lombok.Setter;

/** 游客订单分页条件，当前 MVP 只支持按状态筛选。 */
@Getter
@Setter
public class OrderPageQueryDTO extends PageQuery {

    private BookingOrderStatus status;
}
