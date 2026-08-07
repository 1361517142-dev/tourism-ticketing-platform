package com.qinghuan.coupon;

import com.qinghuan.common.constant.cacheKeys.CouponConstant;
import com.qinghuan.coupon.message.CouponClaimCommand;
import com.qinghuan.pojo.enums.CouponClaimFailureReason;
import com.qinghuan.pojo.enums.CouponClaimStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 抢券失败后的 Redis 修正服务。
 */
@Slf4j
@Service
public class CouponClaimCompensationService {

    private static final DefaultRedisScript<Long> SEND_FAILURE_SCRIPT =
            new DefaultRedisScript<>();

    static {
        SEND_FAILURE_SCRIPT.setLocation(
                new ClassPathResource(
                        "scripts/coupon_claim_compensate.lua"
                )
        );
        SEND_FAILURE_SCRIPT.setResultType(Long.class);
    }

    private final StringRedisTemplate stringRedisTemplate;

    public CouponClaimCompensationService(
            StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * Kafka 消息最终发送失败时，撤销 Redis 预扣。
     */
    public void compensateSendFailure(
            CouponClaimCommand command) {

        Long compensated = stringRedisTemplate.execute(
                SEND_FAILURE_SCRIPT,
                List.of(
                        CouponConstant.userRequestKey(
                                command.activityId(),
                                command.userId()
                        ),
                        CouponConstant.activityStockKey(
                                command.activityId()
                        ),
                        CouponConstant.claimedUsersKey(
                                command.activityId()
                        )
                ),
                command.requestId(),
                command.userId().toString()
        );

        /*
         * 请求已经不能进入数据库处理，
         * 所以无论补偿是否为首次执行，都把当前请求标记为失败。
         */
        markRequestFailed(
                command.requestId(),
                CouponClaimFailureReason.MESSAGE_SEND_FAILED
        );

        log.warn(
                "抢券消息发送失败补偿完成，requestId={}，compensated={}",
                command.requestId(),
                compensated
        );
    }


    private void markRequestFailed(
            String requestId,
            CouponClaimFailureReason failureReason) {

        String resultKey =
                CouponConstant.claimResultKey(requestId);

        stringRedisTemplate.opsForHash().putAll(
                resultKey,
                Map.of(
                        "status",
                        CouponClaimStatus.FAILED.name(),
                        "failureReason",
                        failureReason.name()
                )
        );

        stringRedisTemplate.expire(
                resultKey,
                CouponConstant.CLAIM_RESULT_TTL_SECONDS,
                TimeUnit.SECONDS
        );
    }
}