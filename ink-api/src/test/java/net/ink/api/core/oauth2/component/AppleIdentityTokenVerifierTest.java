package net.ink.api.core.oauth2.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.UnauthorizedException;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AppleIdentityTokenVerifierTest {
    private static final String TEST_KID = "testKid";
    private static final String TEST_ISSUER = "https://appleid.apple.com";
    private static final String TEST_AUDIENCE = "com.with.ink.ink-ios";
    private static final String TEST_SUBJECT = "001234.0123456789abcdef0123456789abcdef.1234";
    private static final String TEST_EMAIL = "test@privaterelay.appleid.com";
    private static final Date NOW = new Date(1790000000000L);

    private static KeyPair appleKeyPair;
    private static KeyPair otherKeyPair;

    @InjectMocks
    private AppleIdentityTokenVerifier appleIdentityTokenVerifier;

    @Mock
    private ApplePublicKeyProvider applePublicKeyProvider;

    @Mock
    private DateFactory dateFactory;

    @BeforeAll
    static void generateKeyPairs() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        appleKeyPair = generator.generateKeyPair();
        otherKeyPair = generator.generateKeyPair();
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appleIdentityTokenVerifier, "issuer", TEST_ISSUER);
        ReflectionTestUtils.setField(appleIdentityTokenVerifier, "audience", TEST_AUDIENCE);
        lenient().when(applePublicKeyProvider.getPublicKey(TEST_KID)).thenReturn(appleKeyPair.getPublic());
        lenient().when(dateFactory.now()).thenReturn(NOW);
    }

    @Test
    void 유효한_토큰_검증_테스트() {
        String token = tokenBuilder().signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        Claims claims = appleIdentityTokenVerifier.verify(token);

        assertEquals(TEST_SUBJECT, claims.getSubject());
        assertEquals(TEST_EMAIL, claims.get("email", String.class));
    }

    @Test
    void 다른_키로_서명된_토큰_테스트() {
        String token = tokenBuilder().signWith(SignatureAlgorithm.RS256, otherKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 알_수_없는_kid_테스트() {
        String token = tokenBuilder().setHeaderParam("kid", "unknownKid")
                .signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 공개키를_대칭키로_사용한_HS256_토큰_테스트() {
        String token = tokenBuilder()
                .signWith(SignatureAlgorithm.HS256, appleKeyPair.getPublic().getEncoded()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 서명_없는_토큰_테스트() {
        String token = tokenBuilder().compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 잘못된_issuer_테스트() {
        String token = tokenBuilder().setIssuer("https://example.com")
                .signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 잘못된_audience_테스트() {
        String token = tokenBuilder().setAudience("com.other.app")
                .signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void 만료된_토큰_테스트() {
        String token = tokenBuilder().setExpiration(new Date(NOW.getTime() - 1000))
                .signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void sub가_없는_토큰_테스트() {
        String token = tokenBuilder().setSubject(null)
                .signWith(SignatureAlgorithm.RS256, appleKeyPair.getPrivate()).compact();

        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(token));
    }

    @Test
    void JWT_형식이_아닌_토큰_테스트() {
        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify("notJwtToken"));
        assertThrows(UnauthorizedException.class, () -> appleIdentityTokenVerifier.verify(null));
    }

    private JwtBuilder tokenBuilder() {
        return Jwts.builder()
                .setHeaderParam("kid", TEST_KID)
                .setIssuer(TEST_ISSUER)
                .setAudience(TEST_AUDIENCE)
                .setSubject(TEST_SUBJECT)
                .claim("email", TEST_EMAIL)
                .setIssuedAt(NOW)
                .setExpiration(new Date(NOW.getTime() + 1000L * 60 * 10));
    }
}
