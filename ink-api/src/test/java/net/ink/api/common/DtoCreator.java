package net.ink.api.common;

import net.ink.api.badge.dto.BadgeDto;
import net.ink.api.member.dto.MemberDto;
import net.ink.api.member.dto.MemberReportDto;
import net.ink.api.question.dto.QuestionDto;
import net.ink.api.question.dto.WordHintDto;
import net.ink.api.reply.dto.ReplyDto;
import net.ink.api.reply.dto.ReplyLikesDto;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.api.todayexpression.dto.UsefulExpressionDto;
import net.ink.core.member.entity.MemberSetting;

import java.util.Set;

public class DtoCreator {
    private static final Long QUESTION_ID = 1L;
    private static final Long MEMBER_ID = 1L;
    private static final Long REPLY_ID = 1L;

    public static MemberDto.ReadOnly createMemberDto() {
        return MemberDto.ReadOnly.builder()
                .memberId(MEMBER_ID)
                .identifier("123")
                .email("test@gmail.com")
                .nickname("Test")
                .inkCount(0)
                .attendanceCount(10)
                .lastAttendanceDate("2020-10-14")
                .memberSetting(MemberSetting.builder()
                        .isLikePushActive(true)
                        .isDailyPushActive(false)
                        .build())
                .build();
    }

    public static QuestionDto.ReadOnly createQuestionDto() {
        return QuestionDto.ReadOnly.builder()
                .questionId(QUESTION_ID)
                .category("카테고리")
                .content("This is english content.")
                .koContent("이것은 한글 내용입니다.")
                .author(createMemberDto())
                .wordHints(Set.of(createWordHintDto()))
                .repliesCount(2)
                .build();
    }

    public static ReplyDto.ReadOnly createReplyDto() {
        return ReplyDto.ReadOnly.builder()
                .questionId(QUESTION_ID)
                .replyId(REPLY_ID)
                .author(createMemberDto())
                .question(createQuestionDto())
                .content("Test Reply")
                .image("image-path")
                .replyLikeCount(2)
                .requestedByAuthor(true)
                .likedByRequester(true)
                .build();
    }

    public static ReplyLikesDto createReplyLikesDto() {
        return ReplyLikesDto.builder()
                .replyId(1L)
                .memberId(1L)
                .build();
    }

    public static WordHintDto.ReadOnly createWordHintDto() {
        return WordHintDto.ReadOnly.builder()
                .hintId(1L)
                .word("apple")
                .meaning("사과")
                .build();
    }

    public static UsefulExpressionDto createTodayExpressionDto() {
        return UsefulExpressionDto.builder()
                .expId(1L)
                .expression("test")
                .meaning("테스트")
                .expressionExample("test driven development")
                .expressionExampleMeaning("테스트 주도 개발")
                .scrappedByRequester(false)
                .build();
    }

    public static BadgeDto createBadgeDto() {
        return BadgeDto.builder()
                .name("Badge Test")
                .content("Badge Test Content")
                .build();
    }

    public static ReplyReportDto.ReadOnly createReplyReportDto() {
        return ReplyReportDto.ReadOnly.builder()
                .reportId(1L)
                .replyId(1L)
                .reporterId(1L)
                .reason("Test Reason")
                .hideToReporter(true)
                .status(net.ink.core.reply.entity.ReplyReport.ProcessStatus.OPEN)
                .method(net.ink.core.reply.entity.ReplyReport.ProcessMethod.PENDING)
                .build();
    }

    public static MemberReportDto.ReadOnly createMemberReportDto() {
        return MemberReportDto.ReadOnly.builder()
                .reportId(1L)
                .targetId(2L)
                .reporterId(1L)
                .reason("Test Reason")
                .hideToReporter(true)
                .build();
    }
}
