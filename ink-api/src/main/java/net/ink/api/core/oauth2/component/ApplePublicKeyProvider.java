package net.ink.api.core.oauth2.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.InkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Apple 공개키(JWKS) 제공자. Apple이 키를 주기적으로 교체하므로 캐싱하되,
 * 캐시가 만료되었거나 캐시에 없는 kid가 들어오면 다시 받아온다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplePublicKeyProvider {
    private static final long CACHE_VALID_MILLISECOND = 1000L * 60 * 60 * 24; // 24 시간
    private static final long MIN_REFRESH_INTERVAL_MILLISECOND = 1000L * 60; // 1 분

    @Value("${spring.social.APPLE.url.keys}")
    private String keysUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DateFactory dateFactory;

    private volatile Map<String, PublicKey> publicKeys = Collections.emptyMap();
    private volatile long fetchedAt = 0;
    private volatile long lastAttemptedAt = 0;

    /**
     * @return kid에 해당하는 공개키, Apple이 제공하는 키 중에 없다면 null
     */
    public PublicKey getPublicKey(String kid) {
        if (isRefreshNeeded(kid)) {
            refresh(kid);
        }
        return publicKeys.get(kid);
    }

    private boolean isRefreshNeeded(String kid) {
        long now = dateFactory.now().getTime();
        boolean expired = now - fetchedAt >= CACHE_VALID_MILLISECOND;
        // 존재하지 않는 kid로 Apple 서버를 반복 호출하지 않도록 최소 간격을 둔다
        return (expired || !publicKeys.containsKey(kid))
                && now - lastAttemptedAt >= MIN_REFRESH_INTERVAL_MILLISECOND;
    }

    private synchronized void refresh(String kid) {
        if (!isRefreshNeeded(kid)) {
            return;
        }

        long now = dateFactory.now().getTime();
        lastAttemptedAt = now;
        try {
            publicKeys = fetchPublicKeys();
            fetchedAt = now;
        } catch (Exception e) {
            if (!publicKeys.containsKey(kid)) {
                throw new InkException("Apple 공개키 조회 중 예외 발생", e);
            }
            log.warn("Apple 공개키 갱신에 실패하여 기존 캐시를 사용합니다.", e);
        }
    }

    private Map<String, PublicKey> fetchPublicKeys() throws Exception {
        JsonNode keys = objectMapper.readTree(restTemplate.getForObject(keysUrl, String.class)).get("keys");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Map<String, PublicKey> fetched = new HashMap<>();
        for (JsonNode key : keys) {
            if (!"RSA".equals(key.path("kty").asText())) {
                continue;
            }
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.get("n").asText()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.get("e").asText()));
            fetched.put(key.get("kid").asText(), keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent)));
        }

        if (fetched.isEmpty()) {
            throw new IllegalStateException("Apple 공개키 응답이 비어있습니다.");
        }
        return fetched;
    }
}
