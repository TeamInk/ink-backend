package net.ink.api.jwt;

import net.ink.api.core.jwt.component.JwtCreator;
import net.ink.api.core.jwt.component.JwtValidator;
import net.ink.core.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JwtAuthenticationTest {
    @Autowired
    JwtCreator jwtCreator;

    @Autowired
    JwtValidator jwtValidator;

    @Test
    public void Jwt_생성(){
        String identifier = "test";
        String nickname = "Test";
        String token = jwtCreator.createAccessToken(
                Member.builder().identifier(identifier).nickname(nickname).build()
        );

        assertTrue(jwtValidator.validateToken(token));
    }

}
