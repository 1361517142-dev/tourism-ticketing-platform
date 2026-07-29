package com.qinghuan.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 的外部化配置。密钥只允许从部署环境注入，不能提交到仓库。
 */
@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secret;
    private String issuer = "tourism-ticketing-platform";
    private Duration accessTokenTtl = Duration.ofHours(2);
    private List<String> excludedPaths = new ArrayList<>();

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths == null ? new ArrayList<>() : new ArrayList<>(excludedPaths);
    }
}
