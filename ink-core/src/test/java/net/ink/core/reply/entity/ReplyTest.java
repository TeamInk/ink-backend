package net.ink.core.reply.entity;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import net.ink.core.annotation.InkDataTest;
import net.ink.core.member.entity.Member;
import net.ink.core.reply.repository.ReplyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@InkDataTest
@DatabaseSetup({
        "classpath:dbunit/entity/member.xml",
        "classpath:dbunit/entity/question.xml",
        "classpath:dbunit/entity/reply.xml",
        "classpath:dbunit/entity/reply_likes.xml"
})
public class ReplyTest {

    @Autowired
    ReplyRepository replyRepository;

    @Test
    @DisplayName("좋아요를 누른 사용자가 요청자인 경우 true를 반환해야 한다")
    public void likedByRequester_좋아요를_누른_사용자가_요청자인_경우_true_반환() {
        // Arrange
        Reply reply = replyRepository.findById(1L).orElseThrow();
        Member member1 = Member.builder().memberId(1L).build();
        Member member2 = Member.builder().memberId(2L).build();

        // Act & Assert
        // reply_likes.xml에서 member_id=1, member_id=2가 reply_id=1에 좋아요를 눌렀음
        assertTrue(reply.likedByRequester(member1), "Member 1이 Reply 1에 좋아요를 눌렀으므로 true여야 함");
        assertTrue(reply.likedByRequester(member2), "Member 2가 Reply 1에 좋아요를 눌렀으므로 true여야 함");
    }

    @Test
    @DisplayName("좋아요를 누르지 않은 사용자가 요청자인 경우 false를 반환해야 한다")
    public void likedByRequester_좋아요를_누르지_않은_사용자가_요청자인_경우_false_반환() {
        // Arrange
        Reply reply = replyRepository.findById(1L).orElseThrow();
        Member member3 = Member.builder().memberId(3L).build();

        // Act & Assert
        // reply_likes.xml에서 member_id=3은 reply_id=1에 좋아요를 누르지 않았음
        assertFalse(reply.likedByRequester(member3), "Member 3이 Reply 1에 좋아요를 누르지 않았으므로 false여야 함");
    }

    @Test
    @DisplayName("좋아요가 없는 답변에서는 항상 false를 반환해야 한다")
    public void likedByRequester_좋아요가_없는_답변에서는_false_반환() {
        // Arrange
        Reply reply = replyRepository.findById(2L).orElseThrow(); // reply_id=2는 좋아요가 없음
        Member member1 = Member.builder().memberId(1L).build();
        Member member2 = Member.builder().memberId(2L).build();

        // Act & Assert
        assertFalse(reply.likedByRequester(member1), "좋아요가 없는 답변에서는 false여야 함");
        assertFalse(reply.likedByRequester(member2), "좋아요가 없는 답변에서는 false여야 함");
    }

    @Test
    @DisplayName("null 사용자에 대해서는 false를 반환해야 한다")
    public void likedByRequester_null_사용자에_대해서는_false_반환() {
        // Arrange
        Reply reply = replyRepository.findById(1L).orElseThrow();

        // Act & Assert
        assertFalse(reply.likedByRequester(null), "null 사용자에 대해서는 false여야 함");
    }

    @Test
    @DisplayName("memberId가 null인 사용자에 대해서는 false를 반환해야 한다")
    public void likedByRequester_memberId가_null인_사용자에_대해서는_false_반환() {
        // Arrange
        Reply reply = replyRepository.findById(1L).orElseThrow();
        Member memberWithNullId = Member.builder().memberId(null).build();

        // Act & Assert
        assertFalse(reply.likedByRequester(memberWithNullId), "memberId가 null인 사용자에 대해서는 false여야 함");
    }
}
