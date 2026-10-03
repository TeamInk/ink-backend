package net.ink.api.core.oauth2.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.InkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * Apple 서버 API(/auth/token, /auth/revoke) 호출에 필요한 client_secret 생성기.
 * client_secret은 Apple에서 발급받은 .p8 키로 서명한 ES256 JWT이다.
 */
@Component
@RequiredArgsConstructor
public class AppleClientSecretCreator {
    private final long CLIENT_SECRET_VALID_MILISECOND = 1000L * 60 * 5; // 5 분

    @Value("${spring.social.APPLE.issuer}")
    private String issuer;

    @Value("${spring.social.APPLE.audience}")
    private String clientId;

    @Value("${spring.social.APPLE.team-id:}")
    private String teamId;

    @Value("${spring.social.APPLE.key-id:}")
    private String keyId;

    @Value("${spring.social.APPLE.private-key:}")
    private String privateKey;

    private final DateFactory dateFactory;

    public String createClientSecret() {
        if (!StringUtils.hasText(teamId) || !StringUtils.hasText(keyId) || !StringUtils.hasText(privateKey)) {
            throw new InkException("Apple 로그인 키 설정(team-id, key-id, private-key)이 없습니다.");
        }

        Date now = dateFactory.now();
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setIssuer(teamId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + CLIENT_SECRET_VALID_MILISECOND))
                .setAudience(issuer)
                .setSubject(clientId)
                .signWith(SignatureAlgorithm.ES256, parsePrivateKey())
                .compact();
    }

    private PrivateKey parsePrivateKey() {
        // .p8 파일 내용 그대로(PEM) 혹은 헤더를 제외한 base64 문자열 모두 허용
        String base64 = privateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
        try {
            return KeyFactory.getInstance("EC")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch (Exception e) {
            throw new InkException("Apple 로그인 private-key 파싱 중 예외 발생", e);
        }
    }
}
