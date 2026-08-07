package com.qinghuan.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 优惠券模块提供给订单模块使用的同步事务能力。 */
public interface CouponOrderService {
    /**
     * 校验券归属、景点、状态、有效期和门槛后原子锁券，并返回订单计价结果。
     * 调用方必须在创建订单的数据库事务中调用。
     */
    CouponDiscount lockForOrder(Long couponId,
                                Long userId,
                                Long venueId,
                                BigDecimal originalAmount);

    /** 支付成功或零元订单创建成功时，将 LOCKED 券转为 USED。 */
    void markUsed(Long couponId, LocalDateTime now);

    /** 待支付订单取消或超时关闭时释放 LOCKED 券。 */
    void releaseLocked(Long couponId, LocalDateTime now);

    /** 已支付订单整单退款时恢复 USED 券。 */
    void restoreAfterRefund(Long couponId, LocalDateTime now);
}
