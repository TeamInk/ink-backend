package net.ink.api.member.service;

import net.ink.api.core.oauth2.service.AppleRevokeService;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.member.entity.Member;
import net.ink.core.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberDropOutServiceTest {
    private static final String TEST_AUTHORIZATION_CODE = "testAuthorizationCode";

    @InjectMocks
    private MemberDropOutService memberDropOutService;

    @Mock
    private MemberService memberService;

    @Mock
    private AppleRevokeService appleRevokeService;

    @Test
    void 애플_회원_탈퇴시_토큰_revoke_테스트() {
        Member member = Member.builder().identifier("apple_001234.abcdef.1234").nickname("Test").build();

        memberDropOutService.dropOut(member, TEST_AUTHORIZATION_CODE);

        InOrder inOrder = inOrder(appleRevokeService, memberService);
        inOrder.verify(appleRevokeService).revoke("apple_001234.abcdef.1234", TEST_AUTHORIZATION_CODE);
        inOrder.verify(memberService).dropOutMember(member);
    }

    @Test
    void 애플_회원_revoke_실패시_탈퇴되지_않는_테스트() {
        Member member = Member.builder().identifier("apple_001234.abcdef.1234").nickname("Test").build();
        doThrow(new BadRequestException("fail")).when(appleRevokeService).revoke("apple_001234.abcdef.1234", null);

        assertThrows(BadRequestException.class, () -> memberDropOutService.dropOut(member, null));

        verify(memberService, never()).dropOutMember(member);
    }

    @Test
    void 카카오_회원_탈퇴시_revoke하지_않는_테스트() {
        Member member = Member.builder().identifier("kakao_1234567890").nickname("Test").build();

        memberDropOutService.dropOut(member, null);

        verifyNoInteractions(appleRevokeService);
        verify(memberService).dropOutMember(member);
    }
}
