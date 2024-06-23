package net.ink.core.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import net.ink.core.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import net.ink.core.member.entity.FcmToken;
import net.ink.core.member.repository.FcmTokenRepository;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {
    @InjectMocks
    FcmTokenService fcmTokenService;

    @Mock
    FcmTokenRepository fcmTokenRepository;

    @Test
    @DisplayName("토큰 삽입 테스트")
    void insertToken() {
        String token = "testToken";

        when(fcmTokenRepository.existsByToken(token)).thenReturn(false);
        when(fcmTokenRepository.saveAndFlush(any(FcmToken.class))).thenReturn(new FcmToken());

        fcmTokenService.insertToken(token);

        verify(fcmTokenRepository, times(1)).saveAndFlush(any(FcmToken.class));
    }

    @Test
    @DisplayName("토큰이 이미 존재할 경우 삽입하지 않음 테스트")
    void insertTokenAlreadyExists() {
        String token = "testToken";

        when(fcmTokenRepository.existsByToken(token)).thenReturn(true);

        fcmTokenService.insertToken(token);

        verify(fcmTokenRepository, never()).saveAndFlush(any(FcmToken.class));
    }

    @Test
    @DisplayName("회원의 토큰 업데이트 테스트")
    void updateTokenOfMember() {
        String token = "newToken";
        Member member = new Member();
        member.setMemberId(1L);

        FcmToken existingToken = new FcmToken();
        existingToken.setIdx(1L);
        existingToken.setToken("oldToken");
        existingToken.setMember(member);

        when(fcmTokenRepository.findByMemberMemberId(member.getMemberId())).thenReturn(Optional.of(existingToken));
        when(fcmTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        when(fcmTokenRepository.saveAndFlush(any(FcmToken.class))).thenReturn(existingToken);

        fcmTokenService.updateTokenOfMember(token, member);

        verify(fcmTokenRepository, times(1)).saveAndFlush(existingToken);
        assertEquals(token, existingToken.getToken());
    }

    @Test
    @DisplayName("회원의 토큰 업데이트 시 기존 토큰 삭제 테스트")
    void updateTokenOfMemberAndDeleteOldToken() {
        String token = "newToken";
        Member member = new Member();
        member.setMemberId(1L);

        FcmToken existingTokenByMember = new FcmToken();
        existingTokenByMember.setIdx(1L);
        existingTokenByMember.setToken("oldToken");
        existingTokenByMember.setMember(member);

        FcmToken existingTokenByToken = new FcmToken();
        existingTokenByToken.setIdx(2L);
        existingTokenByToken.setToken(token);

        when(fcmTokenRepository.findByMemberMemberId(member.getMemberId())).thenReturn(Optional.of(existingTokenByMember));
        when(fcmTokenRepository.findByToken(token)).thenReturn(Optional.of(existingTokenByToken));

        fcmTokenService.updateTokenOfMember(token, member);

        verify(fcmTokenRepository, times(1)).delete(existingTokenByToken);
        verify(fcmTokenRepository, times(1)).saveAndFlush(existingTokenByMember);
        assertEquals(token, existingTokenByMember.getToken());
    }

    @Test
    @DisplayName("회원의 토큰 업데이트 시 새로운 토큰 생성 테스트")
    void updateTokenOfMemberAndCreateNewToken() {
        String token = "newToken";
        Member member = new Member();
        member.setMemberId(1L);

        when(fcmTokenRepository.findByMemberMemberId(member.getMemberId())).thenReturn(Optional.empty());
        when(fcmTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        when(fcmTokenRepository.saveAndFlush(any(FcmToken.class))).thenReturn(new FcmToken());

        fcmTokenService.updateTokenOfMember(token, member);

        verify(fcmTokenRepository, times(1)).saveAndFlush(any(FcmToken.class));
    }
}