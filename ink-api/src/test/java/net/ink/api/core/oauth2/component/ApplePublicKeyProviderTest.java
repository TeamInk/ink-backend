package net.ink.api.core.oauth2.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.InkException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplePublicKeyProviderTest {
    private static final String TEST_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final long NOW = 1790000000000L;
    private static final long ONE_MINUTE = 1000L * 60;
    private static final long ONE_DAY = 1000L * 60 * 60 * 24;

    private static RSAPublicKey firstKey;
    private static RSAPublicKey secondKey;

    private ApplePublicKeyProvider applePublicKeyProvider;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DateFactory dateFactory;

    @BeforeAll
    static void generateKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair first = generator.generateKeyPair();
        KeyPair second = generator.generateKeyPair();
        firstKey = (RSAPublicKey) first.getPublic();
        secondKey = (RSAPublicKey) second.getPublic();
    }

    @BeforeEach
    void setUp() {
        applePublicKeyProvider = new ApplePublicKeyProvider(restTemplate, new ObjectMapper(), dateFactory);
        ReflectionTestUtils.setField(applePublicKeyProvider, "keysUrl", TEST_KEYS_URL);
        when(dateFactory.now()).thenReturn(new Date(NOW));
    }

    @Test
    void 공개키_조회_및_캐싱_테스트() {
        when(restTemplate.getForObject(TEST_KEYS_URL, String.class)).thenReturn(jwks(jwk("first", firstKey)));

        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));
        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));

        verify(restTemplate, times(1)).getForObject(TEST_KEYS_URL, String.class);
    }

    @Test
    void 캐시에_없는_kid는_다시_받아오는_테스트() {
        when(restTemplate.getForObject(TEST_KEYS_URL, String.class))
                .thenReturn(jwks(jwk("first", firstKey)))
                .thenReturn(jwks(jwk("first", firstKey), jwk("second", secondKey)));
        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));

        // 최소 갱신 간격 이내에는 다시 받아오지 않는다
        assertNull(applePublicKeyProvider.getPublicKey("second"));
        verify(restTemplate, times(1)).getForObject(TEST_KEYS_URL, String.class);

        when(dateFactory.now()).thenReturn(new Date(NOW + ONE_MINUTE));
        assertEquals(secondKey, applePublicKeyProvider.getPublicKey("second"));
        verify(restTemplate, times(2)).getForObject(TEST_KEYS_URL, String.class);
    }

    @Test
    void 캐시_만료시_교체된_키로_갱신하는_테스트() {
        when(restTemplate.getForObject(TEST_KEYS_URL, String.class))
                .thenReturn(jwks(jwk("first", firstKey)))
                .thenReturn(jwks(jwk("second", secondKey)));
        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));

        when(dateFactory.now()).thenReturn(new Date(NOW + ONE_DAY));

        assertNull(applePublicKeyProvider.getPublicKey("first"));
        assertEquals(secondKey, applePublicKeyProvider.getPublicKey("second"));
        verify(restTemplate, times(2)).getForObject(TEST_KEYS_URL, String.class);
    }

    @Test
    void 갱신_실패시_기존_캐시를_사용하는_테스트() {
        when(restTemplate.getForObject(TEST_KEYS_URL, String.class))
                .thenReturn(jwks(jwk("first", firstKey)))
                .thenThrow(new ResourceAccessException("timeout"));
        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));

        when(dateFactory.now()).thenReturn(new Date(NOW + ONE_DAY));

        assertEquals(firstKey, applePublicKeyProvider.getPublicKey("first"));
    }

    @Test
    void 캐시된_키가_없을때_조회_실패_테스트() {
        when(restTemplate.getForObject(TEST_KEYS_URL, String.class)).thenThrow(new ResourceAccessException("timeout"));

        assertThrows(InkException.class, () -> applePublicKeyProvider.getPublicKey("first"));
    }

    private String jwks(String... jwks) {
        return "{\"keys\":[" + String.join(",", jwks) + "]}";
    }

    private String jwk(String kid, RSAPublicKey publicKey) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return String.format("{\"kty\":\"RSA\",\"kid\":\"%s\",\"use\":\"sig\",\"alg\":\"RS256\",\"n\":\"%s\",\"e\":\"%s\"}",
                kid,
                encoder.encodeToString(toUnsignedBytes(publicKey.getModulus().toByteArray())),
                encoder.encodeToString(toUnsignedBytes(publicKey.getPublicExponent().toByteArray())));
    }

    private byte[] toUnsignedBytes(byte[] bytes) {
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] unsigned = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, unsigned, 0, unsigned.length);
            return unsigned;
        }
        return bytes;
    }
}
