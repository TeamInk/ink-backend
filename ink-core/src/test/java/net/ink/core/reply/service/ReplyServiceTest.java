package net.ink.core.reply.service;

import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.EntityNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.question.service.QuestionService;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static net.ink.core.core.message.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {

    @InjectMocks
    private ReplyService replyService;

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private QuestionService questionService;

    @Mock
    private ReplyPostProcessService replyPostProcessService;

    private static final Long MEMBER_ID = 1L;
    private static final Long QUESTION_ID = 1L;
    private static final Long REPLY_ID = 1L;

    @Nested
    @DisplayName("답변 등록 테스트")
    class Create {
        @Test
        public void 답변_등록_테스트() {
            // given
            long notRepliedMemberId = 1;

            Reply newReply = EntityCreator.createReplyEntity();

            // mocking
            given(replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(any(), any(), eq(notRepliedMemberId), any(), any())).willReturn(false);
            given(questionService.existsById(eq(QUESTION_ID))).willReturn(true);
            given(replyService.isQuestionAlreadyReplied(eq(QUESTION_ID), eq(notRepliedMemberId))).willReturn(false);
            given(questionService.getQuestionById(eq(QUESTION_ID))).willReturn(newReply.getQuestion());
            given(replyRepository.saveAndFlush(any())).willReturn(newReply);
            doNothing().when(replyPostProcessService).postProcess(any());

            // when
            Reply createdReply = replyService.create(newReply);

            // then
            assertEquals(newReply.getReplyId(), createdReply.getReplyId());
            assertEquals(newReply.getContent(), createdReply.getContent());
            assertEquals(newReply.getImage(), createdReply.getImage());
        }

        @Test
        public void 답변_등록_예외_이미_오늘_답변한_사용자_테스트() {
            // given
            long repliedMemberId = 1;

            Reply newReply = EntityCreator.createReplyEntity();

            // mocking
            given(replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(any(), any(), eq(repliedMemberId), any(), any())).willReturn(true);

            // when
            BadRequestException exception =  assertThrows(BadRequestException.class, () -> replyService.create(newReply));

            // then
            assertEquals(ALREADY_ANSWERED_MEMBER, exception.getMessage());
        }

        @Test
        public void 답변_등록_예외_존재하지않는_질문_테스트(){
            // given
            long notRepliedMemberId = 1;

            Reply newReply = EntityCreator.createReplyEntity();

            // mocking
            given(replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(any(), any(), eq(notRepliedMemberId), any(), any())).willReturn(false);
            given(questionService.existsById(eq(QUESTION_ID))).willReturn(false);

            // when
            EntityNotFoundException exception =  assertThrows(EntityNotFoundException.class, () -> replyService.create(newReply));

            // then
            assertEquals(NOT_EXIST_QUESTION, exception.getMessage());
        }

        @Test
        public void 답변_등록_예외_이미_답변한_질문_테스트() {
            // given
            long repliedMemberId = 1;

            Reply newReply = EntityCreator.createReplyEntity();

            // mocking
            given(replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(any(), any(), eq(MEMBER_ID), any(), any())).willReturn(false);
            given(questionService.existsById(eq(QUESTION_ID))).willReturn(true);
            given(replyService.isQuestionAlreadyReplied(eq(QUESTION_ID), eq(repliedMemberId))).willReturn(true);

            // when
            BadRequestException exception =  assertThrows(BadRequestException.class, () -> replyService.create(newReply));

            // then
            assertEquals(ALREADY_ANSWERED_REPLY, exception.getMessage());
        }

        @Test
        public void 답변_등록_같은_내용_중복_요청은_기존_답변_반환_테스트() {
            // given
            Reply savedReply = EntityCreator.createReplyEntity();
            Reply newReply = EntityCreator.createReplyEntity();
            newReply.setReplyId(null);

            // mocking
            given(replyRepository.findByAuthorMemberIdAndQuestionQuestionIdAndVisibleAndDeleted(eq(MEMBER_ID), eq(QUESTION_ID), eq(true), eq(false)))
                    .willReturn(Optional.of(savedReply));

            // when
            Reply createdReply = replyService.create(newReply);

            // then
            assertSame(savedReply, createdReply);
            verify(replyRepository, never()).saveAndFlush(any());
            verify(replyPostProcessService, never()).postProcess(any());
        }

        @Test
        public void 답변_등록_예외_이미_답변한_질문에_다른_내용_테스트() {
            // given
            Reply savedReply = EntityCreator.createReplyEntity();
            Reply newReply = EntityCreator.createReplyEntity();
            newReply.setReplyId(null);
            newReply.setContent(savedReply.getContent() + " (다른 내용)");

            // mocking
            given(replyRepository.findByAuthorMemberIdAndQuestionQuestionIdAndVisibleAndDeleted(eq(MEMBER_ID), eq(QUESTION_ID), eq(true), eq(false)))
                    .willReturn(Optional.of(savedReply));
            given(replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(any(), any(), eq(MEMBER_ID), any(), any())).willReturn(false);
            given(questionService.existsById(eq(QUESTION_ID))).willReturn(true);
            given(replyService.isQuestionAlreadyReplied(eq(QUESTION_ID), eq(MEMBER_ID))).willReturn(true);

            // when
            BadRequestException exception = assertThrows(BadRequestException.class, () -> replyService.create(newReply));

            // then
            assertEquals(ALREADY_ANSWERED_REPLY, exception.getMessage());
            verify(replyRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Update {
        @Test
        public void 답변_수정_테스트(){
            // given
            LocalDateTime now = LocalDateTime.now();

            Reply newReply = Reply.builder()
                            .replyId(REPLY_ID)
                            .author(EntityCreator.createMemberEntity())
                            .content("New Test Content")
                            .image("New Image Path")
                            .modDate(now)
                            .build();

            Reply oldReply = EntityCreator.createReplyEntity();
            oldReply.modifyReply(newReply);

            given(replyRepository.findById(eq(REPLY_ID))).willReturn(java.util.Optional.ofNullable(oldReply));
            given(replyRepository.saveAndFlush(any())).willReturn(oldReply);

            Reply modifiedReply = replyService.update(newReply);

            // then
            assertEquals(oldReply.getReplyId(), modifiedReply.getReplyId());
            assertEquals(oldReply.getContent(), modifiedReply.getContent());
            assertEquals(oldReply.getImage(), modifiedReply.getImage());
            assertEquals(oldReply.getModDate(), modifiedReply.getModDate());
        }

        @Test
        public void 답변_수정_예외_존재하지않는_질문_테스트(){
            // given
            LocalDateTime now = LocalDateTime.now();

            Reply newReply = Reply.builder()
                            .replyId(REPLY_ID)
                            .author(EntityCreator.createMemberEntity())
                            .content("New Test Content")
                            .image("New Image Path")
                            .modDate(now)
                            .build();

            Member member = EntityCreator.createMemberEntity();
            Reply oldReply = EntityCreator.createReplyEntity();
            oldReply.modifyReply(newReply);

            given(replyRepository.findById(eq(REPLY_ID))).willThrow(new EntityNotFoundException(NOT_EXIST_REPLY));

            // when
            EntityNotFoundException exception =  assertThrows(EntityNotFoundException.class, () -> replyService.update(newReply));

            // then
            assertEquals(NOT_EXIST_REPLY, exception.getMessage());
        }
    }


}

