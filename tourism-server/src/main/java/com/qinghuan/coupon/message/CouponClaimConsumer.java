package com.qinghuan.coupon.message;

import com.qinghuan.common.constant.cacheKeys.CouponConstant;
import com.qinghuan.coupon.CouponClaimConsumerService;
import com.qinghuan.pojo.entity.CouponClaimRequest;
import com.qinghuan.pojo.enums.CouponClaimStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 优惠券抢券消息消费者。
 */
@Slf4j
@Component
public class CouponClaimConsumer {

    private final CouponClaimConsumerService consumerService;
    private final StringRedisTemplate stringRedisTemplate;

    public CouponClaimConsumer(
            CouponClaimConsumerService consumerService,
            StringRedisTemplate stringRedisTemplate) {
        this.consumerService = consumerService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @KafkaListener(
            topics = CouponKafkaConstant.CLAIM_COMMAND_TOPIC,
            groupId = CouponKafkaConstant.CLAIM_CONSUMER_GROUP
    )
    public void consume(CouponClaimCommand command) {
        log.info(
                "开始处理抢券消息，requestId={}，activityId={}，userId={}",
                command.requestId(),
                command.activityId(),
                command.userId()
        );

        /*
         * process 返回时，MySQL 事务已经提交完成。
         * 此时再更新 Redis，避免数据库回滚后 Redis 却显示 SUCCESS。
         */
        CouponClaimRequest result =
                consumerService.process(command);

        updateRedisResult(result);

        log.info(
                "抢券消息处理完成，requestId={}，status={}",
                result.getRequestId(),
                result.getStatus()
        );
    }

    /**
     * MySQL 是最终结果，事务提交后同步 Redis 快速查询状态。
     */
    private void updateRedisResult(CouponClaimRequest result) {
        String resultKey =
                CouponConstant.claimResultKey(result.getRequestId());

        stringRedisTemplate.opsForHash().put(
                resultKey,
                "status",
                result.getStatus().name()
        );

        if (result.getStatus() == CouponClaimStatus.SUCCESS) {
            stringRedisTemplate.opsForHash().put(
                    resultKey,
                    "userCouponId",
                    result.getUserCouponId().toString()
            );
        }

        if (result.getStatus() == CouponClaimStatus.FAILED) {
            stringRedisTemplate.opsForHash().put(
                    resultKey,
                    "failureReason",
                    result.getFailureReason()
            );
        }

        stringRedisTemplate.expire(
                resultKey,
                CouponConstant.CLAIM_RESULT_TTL_SECONDS,
                TimeUnit.SECONDS
        );
    }
}