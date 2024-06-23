package net.ink.core.badge.service;

import static net.ink.core.core.message.ErrorMessage.NOT_EXIST_REPLY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import net.ink.core.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.ink.core.badge.entity.BadgeAccomplished;
import net.ink.core.badge.repository.BadgeAccomplishedRepository;
import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.member.repository.MemberRepository;
import net.ink.core.member.repository.MemberScrapRepository;
import net.ink.core.member.service.MemberService;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;

@ExtendWith(MockitoExtension.class)
class BadgeAccomplishedServiceImplTest {
    @InjectMocks
    BadgeAccomplishedServiceImpl badgeAccomplishedService;

    @Mock
    ReplyRepository replyRepository;

    @Mock
    MemberService memberService;

    @Mock
    BadgeService badgeService;

    @Mock
    BadgeAccomplishedRepository badgeAccomplishedRepository;

    @Mock
    MemberScrapRepository memberScrapRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("Ink3Days 배지 생성 테스트")
    void createInk3Days() {
        Member member = EntityCreator.createMemberEntity();
        member.setInkCookies(Set.of(EntityCreator.createCookieAcquirementEntity(),
                EntityCreator.createCookieAcquirementEntity(),
                EntityCreator.createCookieAcquirementEntity()));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createInk3Days(1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("CelebrityInk 배지 생성 테스트")
    void createCelebrityInk() {
        Reply reply = EntityCreator.createReplyEntity();
        for (int i = 0; i < 10; i++) {
            reply.getReplyLikes().add(EntityCreator.createReplyLikesEntity());
        }
        when(replyRepository.findById(anyLong())).thenReturn(Optional.of(reply));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createCelebrityInk(1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("존재하지 않는 Reply로 CelebrityInk 배지 생성 실패 테스트")
    void createCelebrityInkNotFound() {
        when(replyRepository.findById(anyLong())).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> badgeAccomplishedService.createCelebrityInk(1L));
        assertEquals(NOT_EXIST_REPLY, exception.getMessage());
    }

    @Test
    @DisplayName("AcademicInk 배지 생성 테스트")
    void createAcademicInk() {
        when(memberScrapRepository.countByMemberMemberId(anyLong())).thenReturn(3L);
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createAcademicInk(1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("InkSet 배지 생성 테스트")
    void createInkSet() {
        Member member = EntityCreator.createMemberEntity();
        for (int i = 0; i < 30; i++) {
            member.getInkCookies().add(EntityCreator.createCookieAcquirementEntity());
        }
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(badgeAccomplishedRepository.existsBadgeAccomplishedByMemberMemberIdAndBadgeBadgeId(anyLong(), anyLong())).thenReturn(false);

        boolean result = badgeAccomplishedService.createInkSet(1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("CelebrityInk 배지 이미 확인된 경우 테스트")
    void isAlreadyCheckedCelebrityInk() {
        BadgeAccomplished badgeAccomplished = new BadgeAccomplished();
        badgeAccomplished.setIsChecked(true);
        when(badgeAccomplishedRepository.findById(any())).thenReturn(Optional.of(badgeAccomplished));

        boolean result = badgeAccomplishedService.isAlreadyCheckedCelebrityInk(1L);
        assertFalse(result);
    }
}