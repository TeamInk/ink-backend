package net.ink.core.badge.service;

import static org.junit.jupiter.api.Assertions.*;

import net.ink.core.badge.entity.BadgeAccomplished;
import net.ink.core.badge.repository.BadgeAccomplishedRepository;
import net.ink.core.common.EntityCreator;
import net.ink.core.cookie.entity.CookieAcquirement;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;
import net.ink.core.member.repository.MemberScrapRepository;
import net.ink.core.member.service.MemberService;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyLikes;
import net.ink.core.reply.repository.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.parser.Entity;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadgeAccomplishedServiceImplTest {

    @InjectMocks
    private BadgeAccomplishedServiceImpl badgeAccomplishedService;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private BadgeService badgeService;

    @Mock
    private BadgeAccomplishedRepository badgeAccomplishedRepository;

    @Mock
    private MemberScrapRepository memberScrapRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("createInk3Days 성공")
    public void createInk3Days_shouldReturnTrue_whenConditionsAreMet() {
        Member member = EntityCreator.createMemberEntity();
        Set<CookieAcquirement> cookieAcquirements = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            cookieAcquirements.add(EntityCreator.createCookieAcquirementEntity());
        }
        member.setInkCookies(cookieAcquirements);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createInk3Days(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("createInk3Days 실패 - 조건이 충족되지 않음")
    public void createInk3Days_shouldReturnFalse_whenConditionsAreNotMet() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(EntityCreator.createMemberEntity()));

        boolean result = badgeAccomplishedService.createInk3Days(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("createCelebrityInk 성공")
    public void createCelebrityInk_shouldReturnTrue_whenConditionsAreMet() {
        Reply reply = EntityCreator.createReplyEntity();
        Set<ReplyLikes> replyLikes = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            replyLikes.add(EntityCreator.createReplyLikesEntity());
        }
        reply.setReplyLikes(replyLikes);
        when(replyRepository.findById(anyLong())).thenReturn(Optional.of(reply));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createCelebrityInk(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("createCelebrityInk 실패 - 조건이 충족되지 않음")
    public void createCelebrityInk_shouldReturnFalse_whenConditionsAreNotMet() {
        when(replyRepository.findById(anyLong())).thenReturn(Optional.of(EntityCreator.createReplyEntity()));

        boolean result = badgeAccomplishedService.createCelebrityInk(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("createAcademicInk 성공")
    public void createAcademicInk_shouldReturnTrue_whenConditionsAreMet() {
        when(memberScrapRepository.countByMemberMemberId(anyLong())).thenReturn(3L);
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createAcademicInk(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("createAcademicInk 실패 - 조건이 충족되지 않음")
    public void createAcademicInk_shouldReturnFalse_whenConditionsAreNotMet() {
        when(memberScrapRepository.countByMemberMemberId(anyLong())).thenReturn(3L);
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(true);

        boolean result = badgeAccomplishedService.createAcademicInk(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("createInkSet 성공")
    public void createInkSet_shouldReturnTrue_whenConditionsAreMet() {
        Member member = EntityCreator.createMemberEntity();
        Set<CookieAcquirement> cookieAcquirements = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            cookieAcquirements.add(EntityCreator.createCookieAcquirementEntity());
        }
        member.setInkCookies(cookieAcquirements);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createInkSet(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("createInkSet 실패 - 조건이 충족되지 않음")
    public void createInkSet_shouldReturnFalse_whenConditionsAreNotMet() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(EntityCreator.createMemberEntity()));

        boolean result = badgeAccomplishedService.createInkSet(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("isAlreadyCheckedCelebrityInk 성공")
    public void isAlreadyCheckedCelebrityInk_shouldReturnTrue_whenBadgeIsNotChecked() {
        when(badgeAccomplishedRepository.findById(any())).thenReturn(Optional.of(EntityCreator.createBadgeAccomplishedEntity()));

        boolean result = badgeAccomplishedService.isAlreadyCheckedCelebrityInk(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("isAlreadyCheckedCelebrityInk 실패 - 뱃지를 획득하지 않았거나 혹은 획득했는데 이미 확인한 경우")
    public void isAlreadyCheckedCelebrityInk_shouldReturnFalse_whenBadgeIsCheckedOrDoesNotExist() {
        when(badgeAccomplishedRepository.findById(any())).thenReturn(Optional.empty());

        boolean result = badgeAccomplishedService.isAlreadyCheckedCelebrityInk(1L);

        assertFalse(result);
    }
}