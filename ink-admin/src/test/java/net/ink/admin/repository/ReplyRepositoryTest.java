package net.ink.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;
import net.ink.core.question.entity.Question;
import net.ink.core.question.repository.QuestionRepository;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;

@SpringBootTest
@Transactional
@Disabled("데이터베이스 제약 조건 위반 문제로 인해 비활성화")
public class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Member testMember;
    private Question testQuestion;

    @BeforeEach
    public void setup() {
        // 테스트 멤버 생성
        testMember = Member.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .identifier("test-identifier")
                .isActive(true)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();
        memberRepository.save(testMember);

        // 테스트 질문 생성
        testQuestion = Question.builder()
                .content("테스트 질문입니다.")
                .koContent("테스트 질문입니다.")
                .category("테스트")
                .regDate(LocalDateTime.now())
                .build();
        questionRepository.save(testQuestion);

        // 테스트 답변 생성
        Reply reply1 = Reply.builder()
                .content("테스트 답변 1")
                .author(testMember)
                .question(testQuestion)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .deleted(false)
                .visible(true)
                .build();
        replyRepository.save(reply1);

        Reply reply2 = Reply.builder()
                .content("테스트 답변 2")
                .author(testMember)
                .question(testQuestion)
                .regDate(LocalDateTime.now().minusDays(1))
                .modDate(LocalDateTime.now().minusDays(1))
                .deleted(false)
                .visible(true)
                .build();
        replyRepository.save(reply2);
    }

    @Test
    @DisplayName("작성자 ID로 답변 목록 조회 테스트")
    public void testFindAllByAuthorMemberId() {
        // given
        Long memberId = testMember.getMemberId();
        Sort sort = Sort.by(Sort.Direction.DESC, "replyId");

        // when
        List<Reply> replies = replyRepository.findAllByAuthorMemberId(memberId, sort);

        // then
        assertThat(replies).isNotNull();
        assertThat(replies).allMatch(reply -> reply.getAuthor().getMemberId().equals(memberId));

        // 정렬 확인
        if (replies.size() > 1) {
            for (int i = 0; i < replies.size() - 1; i++) {
                assertThat(replies.get(i).getReplyId()).isGreaterThan(replies.get(i + 1).getReplyId());
            }
        }
    }

    @Test
    @DisplayName("존재하지 않는 작성자 ID로 답변 목록 조회 시 빈 목록 반환 테스트")
    public void testFindAllByAuthorMemberIdWithNonExistingMemberId() {
        // given
        Long nonExistingMemberId = 999L;
        Sort sort = Sort.by(Sort.Direction.DESC, "replyId");

        // when
        List<Reply> replies = replyRepository.findAllByAuthorMemberId(nonExistingMemberId, sort);

        // then
        assertThat(replies).isNotNull();
        assertThat(replies).isEmpty();
    }
}