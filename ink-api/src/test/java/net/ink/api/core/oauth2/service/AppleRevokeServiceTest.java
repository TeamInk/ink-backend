package net.ink.api.core.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import net.ink.api.core.oauth2.component.AppleClientSecretCreator;
import net.ink.api.core.oauth2.component.AppleIdentityTokenVerifier;
import net.ink.core.core.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppleRevokeServiceTest {
    private static final String TEST_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String TEST_REVOKE_URL = "https://appleid.apple.com/auth/revoke";
    private static final String TEST_CLIENT_ID = "com.with.ink.ink-ios";
    private static final String TEST_CLIENT_SECRET = "testClientSecret";
    private static final String TEST_AUTHORIZATION_CODE = "testAuthorizationCode";
    private static final String TEST_ID_TOKEN = "testIdToken";
    private static final String TEST_SUBJECT = "001234.0123456789abcdef0123456789abcdef.1234";
    private static final String TEST_IDENTIFIER = "apple_" + TEST_SUBJECT;
    private static final String TEST_TOKEN_RESPONSE = "{\"access_token\":\"testAccessToken\"," +
            "\"refresh_token\":\"testRefreshToken\",\"id_token\":\"" + TEST_ID_TOKEN + "\"}";

    private AppleRevokeService appleRevokeService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppleClientSecretCreator appleClientSecretCreator;

    @Mock
    private AppleIdentityTokenVerifier appleIdentityTokenVerifier;

    @BeforeEach
    void setUp() {
        appleRevokeService = new AppleRevokeService(restTemplate, new ObjectMapper(),
                appleClientSecretCreator, appleIdentityTokenVerifier);
        ReflectionTestUtils.setField(appleRevokeService, "tokenUrl", TEST_TOKEN_URL);
        ReflectionTestUtils.setField(appleRevokeService, "revokeUrl", TEST_REVOKE_URL);
        ReflectionTestUtils.setField(appleRevokeService, "clientId", TEST_CLIENT_ID);
        lenient().when(appleClientSecretCreator.createClientSecret()).thenReturn(TEST_CLIENT_SECRET);
        lenient().when(appleIdentityTokenVerifier.verify(TEST_ID_TOKEN))
                .thenReturn(Jwts.claims().setSubject(TEST_SUBJECT));
    }

    @Test
    @SuppressWarnings("unchecked")
    void authorizationCode를_교환한_토큰을_revoke하는_테스트() {
        when(restTemplate.postForObject(eq(TEST_TOKEN_URL), any(), eq(String.class))).thenReturn(TEST_TOKEN_RESPONSE);

        appleRevokeService.revoke(TEST_IDENTIFIER, TEST_AUTHORIZATION_CODE);

        ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> tokenRequest = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForObject(eq(TEST_TOKEN_URL), tokenRequest.capture(), eq(String.class));
        MultiValueMap<String, String> tokenParams = tokenRequest.getValue().getBody();
        assertEquals(TEST_CLIENT_ID, tokenParams.getFirst("client_id"));
        assertEquals(TEST_CLIENT_SECRET, tokenParams.getFirst("client_secret"));
        assertEquals(TEST_AUTHORIZATION_CODE, tokenParams.getFirst("code"));
        assertEquals("authorization_code", tokenParams.getFirst("grant_type"));

        ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> revokeRequest = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForObject(eq(TEST_REVOKE_URL), revokeRequest.capture(), eq(String.class));
        MultiValueMap<String, String> revokeParams = revokeRequest.getValue().getBody();
        assertEquals(TEST_CLIENT_ID, revokeParams.getFirst("client_id"));
        assertEquals(TEST_CLIENT_SECRET, revokeParams.getFirst("client_secret"));
        assertEquals("testRefreshToken", revokeParams.getFirst("token"));
        assertEquals("refresh_token", revokeParams.getFirst("token_type_hint"));
    }

    @Test
    void authorizationCode가_없을때_예외_테스트() {
        assertThrows(BadRequestException.class, () -> appleRevokeService.revoke(TEST_IDENTIFIER, null));
        assertThrows(BadRequestException.class, () -> appleRevokeService.revoke(TEST_IDENTIFIER, " "));

        verifyNoInteractions(restTemplate);
    }

    @Test
    void 유효하지_않은_authorizationCode_예외_테스트() {
        when(restTemplate.postForObject(eq(TEST_TOKEN_URL), any(), eq(String.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null,
                        "{\"error\":\"invalid_grant\"}".getBytes(), null));

        assertThrows(BadRequestException.class,
                () -> appleRevokeService.revoke(TEST_IDENTIFIER, TEST_AUTHORIZATION_CODE));

        verify(restTemplate, never()).postForObject(eq(TEST_REVOKE_URL), any(), eq(String.class));
    }

    @Test
    void 다른_사용자의_authorizationCode_예외_테스트() {
        when(restTemplate.postForObject(eq(TEST_TOKEN_URL), any(), eq(String.class))).thenReturn(TEST_TOKEN_RESPONSE);

        assertThrows(BadRequestException.class,
                () -> appleRevokeService.revoke("apple_otherSubject", TEST_AUTHORIZATION_CODE));

        verify(restTemplate, never()).postForObject(eq(TEST_REVOKE_URL), any(), eq(String.class));
    }
}
