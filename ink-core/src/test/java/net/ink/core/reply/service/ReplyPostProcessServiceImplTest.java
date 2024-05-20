package net.ink.core.reply.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.ink.core.common.EntityCreator;
import net.ink.core.cookie.service.CookieAcquirementService;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.service.ReplyPostProcessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplyPostProcessServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CookieAcquirementService cookieAcquirementService;

    @InjectMocks
    private ReplyPostProcessServiceImpl replyPostProcessService;

    private final Reply reply = EntityCreator.createReplyEntity();
    private final Member member = reply.getAuthor();

    @Test
    @DisplayName("Should acquire cookie when not already acquired today")
    void shouldAcquireCookieWhenNotAlreadyAcquiredToday() {
        when(cookieAcquirementService.isAlreadyCookieAcquiredToday(member.getMemberId())).thenReturn(false);

        replyPostProcessService.postProcess(reply);

        verify(cookieAcquirementService, times(1)).acquireInkCookie(reply.getAuthor());
        verify(memberRepository, times(1)).saveAndFlush(reply.getAuthor());
    }

    @Test
    @DisplayName("Should not acquire cookie when already acquired today")
    void shouldNotAcquireCookieWhenAlreadyAcquiredToday() {
        when(cookieAcquirementService.isAlreadyCookieAcquiredToday(member.getMemberId())).thenReturn(true);

        replyPostProcessService.postProcess(reply);

        verify(cookieAcquirementService, times(0)).acquireInkCookie(reply.getAuthor());
        verify(memberRepository, times(1)).saveAndFlush(reply.getAuthor());
    }
}