package net.ink.api.core.oauth2.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.InkException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AppleClientSecretCreatorTest {
    private static final String TEST_ISSUER = "https://appleid.apple.com";
    private static final String TEST_CLIENT_ID = "com.with.ink.ink-ios";
    private static final String TEST_TEAM_ID = "TESTTEAMID";
    private static final String TEST_KEY_ID = "TESTKEYID";
    private static final Date NOW = new Date(1790000000000L);

    private static KeyPair keyPair;

    @InjectMocks
    private AppleClientSecretCreator appleClientSecretCreator;

    @Mock
    private DateFactory dateFactory;

    @BeforeAll
    static void generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        keyPair = generator.generateKeyPair();
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appleClientSecretCreator, "issuer", TEST_ISSUER);
        ReflectionTestUtils.setField(appleClientSecretCreator, "clientId", TEST_CLIENT_ID);
        ReflectionTestUtils.setField(appleClientSecretCreator, "teamId", TEST_TEAM_ID);
        ReflectionTestUtils.setField(appleClientSecretCreator, "keyId", TEST_KEY_ID);
        ReflectionTestUtils.setField(appleClientSecretCreator, "privateKey", toPem(keyPair));
        lenient().when(dateFactory.now()).thenReturn(NOW);
    }

    @Test
    void client_secret_생성_테스트() {
        String clientSecret = appleClientSecretCreator.createClientSecret();

        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .setClock(() -> NOW)
                .parseClaimsJws(clientSecret);
        assertEquals("ES256", jws.getHeader().getAlgorithm());
        assertEquals(TEST_KEY_ID, jws.getHeader().getKeyId());
        assertEquals(TEST_TEAM_ID, jws.getBody().getIssuer());
        assertEquals(TEST_CLIENT_ID, jws.getBody().getSubject());
        assertEquals(TEST_ISSUER, jws.getBody().getAudience());
        assertEquals(NOW, jws.getBody().getIssuedAt());
        assertEquals(new Date(NOW.getTime() + 1000L * 60 * 5), jws.getBody().getExpiration());
    }

    @Test
    void 헤더_없는_base64_키로_생성_테스트() {
        ReflectionTestUtils.setField(appleClientSecretCreator, "privateKey",
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

        String clientSecret = appleClientSecretCreator.createClientSecret();

        assertDoesNotThrow(() -> Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .setClock(() -> NOW)
                .parseClaimsJws(clientSecret));
    }

    @Test
    void 키_설정이_없을때_예외_테스트() {
        ReflectionTestUtils.setField(appleClientSecretCreator, "privateKey", "");

        assertThrows(InkException.class, () -> appleClientSecretCreator.createClientSecret());
    }

    @Test
    void 잘못된_키_예외_테스트() {
        ReflectionTestUtils.setField(appleClientSecretCreator, "privateKey", "invalidKey");

        assertThrows(InkException.class, () -> appleClientSecretCreator.createClientSecret());
    }

    private static String toPem(KeyPair keyPair) {
        return "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
    }
}
