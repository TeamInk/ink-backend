package net.ink.api.core.oauth2.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import net.ink.core.core.component.DateFactory;
import net.ink.core.core.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.security.PublicKey;

/**
 * Apple identity token 검증기. Apple 개인키로 서명된 RS256 JWT를
 * Apple 공개키로 검증하고 iss, aud, exp 클레임을 확인한다.
 */
@Component
@RequiredArgsConstructor
public class AppleIdentityTokenVerifier {
    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 Apple 토큰입니다.";

    @Value("${spring.social.APPLE.issuer}")
    private String issuer;

    @Value("${spring.social.APPLE.audience}")
    private String audience;

    private final ApplePublicKeyProvider applePublicKeyProvider;
    private final DateFactory dateFactory;

    public Claims verify(String identityToken) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKeyResolver(new ApplePublicKeyResolver())
                    .setClock(dateFactory::now)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .parseClaimsJws(identityToken)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(INVALID_TOKEN_MESSAGE, e);
        }

        if (!StringUtils.hasText(claims.getSubject())) {
            throw new UnauthorizedException(INVALID_TOKEN_MESSAGE);
        }
        return claims;
    }

    private class ApplePublicKeyResolver extends SigningKeyResolverAdapter {
        @Override
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            if (!SignatureAlgorithm.RS256.getValue().equals(header.getAlgorithm())) {
                throw new UnsupportedJwtException("Apple identity token은 RS256 서명만 허용합니다.");
            }

            PublicKey publicKey = applePublicKeyProvider.getPublicKey(header.getKeyId());
            if (publicKey == null) {
                throw new UnsupportedJwtException("kid에 해당하는 Apple 공개키가 없습니다.");
            }
            return publicKey;
        }
    }
}
