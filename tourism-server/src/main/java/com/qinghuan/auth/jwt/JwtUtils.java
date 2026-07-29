package com.qinghuan.auth.jwt;

import com.qinghuan.auth.config.JwtProperties;
import com.qinghuan.auth.model.LoginUser;
import com.qinghuan.pojo.enums.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 负责 Access Token 的签发和签名校验，不处理账号密码校验。
 */
@Component
public class JwtUtils {

    private static final String LOGIN_NAME_CLAIM = "loginName";
    private static final String ROLE_CODE_CLAIM = "roleCode";
    private static final String VENUE_ID_CLAIM = "venueId";

    private final JwtProperties properties;
    private final SecretKey signingKey;
    private final JwtParser parser;
    private final Clock clock;

    @Autowired
    public JwtUtils(JwtProperties properties) {
        this(properties, Clock.systemUTC());
    }

    JwtUtils(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        validateProperties(properties);
        this.signingKey = createSigningKey(properties.getSecret());
        this.parser = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.getIssuer())
                .build();
    }

    public String generateAccessToken(LoginUser loginUser) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(properties.getAccessTokenTtl());

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(loginUser.userId()))
                .claim(LOGIN_NAME_CLAIM, loginUser.loginName())
                .claim(ROLE_CODE_CLAIM, loginUser.roleCode().name())
                .claim(VENUE_ID_CLAIM, loginUser.venueId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public LoginUser parseAccessToken(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();

        try {
            Long userId = Long.valueOf(claims.getSubject());
            String loginName = claims.get(LOGIN_NAME_CLAIM, String.class);
            String roleCode = claims.get(ROLE_CODE_CLAIM, String.class);
            Long venueId = readNullableLong(claims.get(VENUE_ID_CLAIM));
            return new LoginUser(userId, loginName, AccountRole.valueOf(roleCode), venueId);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new JwtException("JWT claims are invalid", exception);
        }
    }

    private static Long readNullableLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private static SecretKey createSigningKey(String base64Secret) {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("security.jwt.secret must be a Base64-encoded key of at least 32 bytes", exception);
        }
    }

    private static void validateProperties(JwtProperties properties) {
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new IllegalStateException("security.jwt.secret must be configured");
        }
        if (properties.getIssuer() == null || properties.getIssuer().isBlank()) {
            throw new IllegalStateException("security.jwt.issuer must be configured");
        }
        Duration accessTokenTtl = properties.getAccessTokenTtl();
        if (accessTokenTtl == null || accessTokenTtl.isNegative() || accessTokenTtl.isZero()) {
            throw new IllegalStateException("security.jwt.access-token-ttl must be positive");
        }
    }
}
