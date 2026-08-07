package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.CouponClaimStatus;

/**
 * 抢券请求被 Redis 接受后的返回结果。
 *
 * PENDING 只代表已经取得异步处理资格，
 * 不代表数据库已经成功发放优惠券。
 */
public record CouponClaimAcceptedVO(
        String requestId,
        CouponClaimStatus status) {
}