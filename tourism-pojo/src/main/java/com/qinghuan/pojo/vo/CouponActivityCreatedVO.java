package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.CouponActivityStatus;

/** 创建活动草稿后的最小响应。 */
public record CouponActivityCreatedVO(Long id, CouponActivityStatus status) {
}
