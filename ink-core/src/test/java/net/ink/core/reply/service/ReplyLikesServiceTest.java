package net.ink.core.reply.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import net.ink.core.badge.service.BadgeAccomplishedService;
import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.InkException;
import net.ink.core.member.entity.Member;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyLikes;
import net.ink.core.reply.entity.ReplyLikesPK;
import net.ink.core.reply.repository.ReplyLikesRepository;
import net.ink.push.service.FcmLikePushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplyLikesServiceTest {

    @Mock
    private ReplyLikesRepository replyLikesRepository;

    @Mock
    private ReplyService replyService;

    @Mock
    private BadgeAccomplishedService badgeAccomplishedService;

    @Mock
    private FcmLikePushService fcmLikePushService;

    @InjectMocks
    private ReplyLikesService replyLikesService;

    private final Reply reply = EntityCreator.createReplyEntity();
    private final Member member = reply.getAuthor();
    private final ReplyLikesPK replyLikesPK = new ReplyLikesPK(member.getMemberId(), reply.getReplyId());

    @BeforeEach
    void setUp() {
        when(replyService.findById(anyLong())).thenReturn(reply);
    }

    @Test
    @DisplayName("Should post reply likes when not already liked")
    void shouldPostReplyLikesWhenNotAlreadyLiked() {
        when(replyLikesRepository.existsById(replyLikesPK)).thenReturn(false);
        when(badgeAccomplishedService.createCelebrityInk(anyLong())).thenReturn(true);
        doNothing().when(fcmLikePushService).pushToAuthor(any(Reply.class), any(Member.class));

        replyLikesService.postReplyLikes(member, reply.getReplyId());

        verify(replyLikesRepository, times(1)).saveAndFlush(any(ReplyLikes.class));
    }

    @Test
    @DisplayName("Should throw exception when reply already liked")
    void shouldThrowExceptionWhenReplyAlreadyLiked() {
        when(replyLikesRepository.existsById(replyLikesPK)).thenReturn(true);

        assertThrows(InkException.class, () -> replyLikesService.postReplyLikes(member, reply.getReplyId()));
    }

    @Test
    @DisplayName("Should delete reply likes when already liked")
    void shouldDeleteReplyLikesWhenAlreadyLiked() {
        when(replyLikesRepository.existsById(replyLikesPK)).thenReturn(true);

        replyLikesService.deleteReplyLikes(member, reply.getReplyId());

        verify(replyLikesRepository, times(1)).deleteById(replyLikesPK);
    }

    @Test
    @DisplayName("Should throw exception when reply not liked")
    void shouldThrowExceptionWhenReplyNotLiked() {
        when(replyLikesRepository.existsById(replyLikesPK)).thenReturn(false);

        assertThrows(InkException.class, () -> replyLikesService.deleteReplyLikes(member, reply.getReplyId()));
    }
}