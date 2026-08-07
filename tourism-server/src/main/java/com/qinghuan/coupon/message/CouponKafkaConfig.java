package com.qinghuan.coupon.message;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * 优惠券相关 Kafka Topic 配置。
 */
@Configuration
public class CouponKafkaConfig {

    /**
     * 抢券命令 Topic。
     *
     * 本地只有一个 Kafka Broker，所以副本数设置为 1。
     */
    @Bean
    public NewTopic couponClaimCommandTopic() {
        return TopicBuilder
                .name(CouponKafkaConstant.CLAIM_COMMAND_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}