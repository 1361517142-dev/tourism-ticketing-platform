package com.qinghuan.Task;

import com.qinghuan.coupon.CouponActivityService;
import com.qinghuan.coupon.CouponQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** 只维护数据库终态，不包含开抢前 Redis 自动预热。 */
@Slf4j
@Component
public class CouponMaintenanceTask {

    private final CouponActivityService activityService;
    private final CouponQueryService queryService;

    public CouponMaintenanceTask(CouponActivityService activityService,
                                 CouponQueryService queryService) {
        this.activityService = activityService;
        this.queryService = queryService;
    }

    @Scheduled(cron = "15 * * * * *")
    public void maintainDatabaseStatuses() {
        // 两次批量更新共用同一时间基准，避免分钟边界附近出现判断差异。
        LocalDateTime now = LocalDateTime.now();
        // 本任务只维护 MySQL 展示状态，不承担 Redis 预热、补偿或对账职责。
        int endedActivities = activityService.endExpiredActivities(now);
        int expiredCoupons = queryService.expireAvailableCoupons(now);
        if (endedActivities > 0 || expiredCoupons > 0) {
            log.info("优惠券状态维护完成：结束活动 {} 个，过期优惠券 {} 张", endedActivities, expiredCoupons);
        }
    }
}
