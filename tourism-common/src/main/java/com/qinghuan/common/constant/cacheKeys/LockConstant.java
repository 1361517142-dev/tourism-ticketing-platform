package com.qinghuan.common.constant.cacheKeys;

public class LockConstant {
    // 订单业务相关锁前缀
    public static final String LOCK_BOOKING_PREFIX = "lock:booking:";

    /**
     * 优惠券活动预热锁。
     *
     * 同一活动只允许一个服务实例执行预热，避免多个定时任务
     * 同时覆盖 Redis 数据和重复修改 preheated_at。
     */
    public static final String LOCK_COUPON_PREHEAT_PREFIX =
            "lock:coupon:preheat:";
}
