package net.ink.api.core.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.oauth2.component.AppleClientSecretCreator;
import net.ink.api.core.oauth2.component.AppleIdentityTokenVerifier;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.InkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Apple 회원 탈퇴 시 Apple 토큰을 revoke 한다. (App Store 심사 가이드라인 5.1.1(v))
 * 클라이언트가 탈퇴 직전 재인증으로 받은 authorizationCode를 토큰으로 교환한 뒤 revoke 한다.
 */
@Service
@RequiredArgsConstructor
public class AppleRevokeService {
    public static final String IDENTIFIER_PREFIX = "apple_";

    @Value("${spring.social.APPLE.url.token}")
    private String tokenUrl;

    @Value("${spring.social.APPLE.url.revoke}")
    private String revokeUrl;

    @Value("${spring.social.APPLE.audience}")
    private String clientId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppleClientSecretCreator appleClientSecretCreator;
    private final AppleIdentityTokenVerifier appleIdentityTokenVerifier;

    public void revoke(String identifier, String authorizationCode) {
        if (!StringUtils.hasText(authorizationCode)) {
            throw new BadRequestException("Apple 회원 탈퇴에는 authorizationCode가 필요합니다.");
        }

        String clientSecret = appleClientSecretCreator.createClientSecret();
        JsonNode token = exchangeToken(clientSecret, authorizationCode);

        Claims claims = appleIdentityTokenVerifier.verify(token.path("id_token").asText(null));
        if (!identifier.equals(IDENTIFIER_PREFIX + claims.getSubject())) {
            throw new BadRequestException("authorizationCode가 로그인한 사용자의 것이 아닙니다.");
        }

        if (token.hasNonNull("refresh_token")) {
            revokeToken(clientSecret, token.get("refresh_token").asText(), "refresh_token");
        } else {
            revokeToken(clientSecret, token.path("access_token").asText(null), "access_token");
        }
    }

    private JsonNode exchangeToken(String clientSecret, String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        try {
            return objectMapper.readTree(restTemplate.postForObject(tokenUrl, formRequest(params), String.class));
        } catch (HttpClientErrorException.BadRequest e) {
            // 만료되었거나 이미 사용된 authorizationCode (invalid_grant)
            throw new BadRequestException("유효하지 않은 Apple authorizationCode 입니다.");
        } catch (JsonProcessingException e) {
            throw new InkException("Provider 응답 호출 중 예외 발생", e);
        }
    }

    private void revokeToken(String clientSecret, String token, String tokenTypeHint) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", token);
        params.add("token_type_hint", tokenTypeHint);

        restTemplate.postForObject(revokeUrl, formRequest(params), String.class);
    }

    private HttpEntity<MultiValueMap<String, String>> formRequest(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(params, headers);
    }
}
