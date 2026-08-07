package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.CouponActivityStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 运营端优惠券活动分页筛选条件。 */
@Getter
@Setter
public class CouponActivityPageQueryDTO extends PageQuery {
    private CouponActivityStatus status;

    @Size(max = 100, message = "关键字不能超过100个字符")
    private String keyword;
}
