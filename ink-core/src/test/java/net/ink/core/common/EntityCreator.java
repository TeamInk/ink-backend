package net.ink.core.common;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.ink.core.badge.entity.Badge;
import net.ink.core.badge.entity.BadgeAccomplished;
import net.ink.core.badge.entity.BadgeAccomplishedPK;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.entity.MemberScrap;
import net.ink.core.member.entity.MemberScrapPK;
import net.ink.core.question.entity.Question;
import net.ink.core.question.entity.WordHint;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyLikes;
import net.ink.core.reply.entity.ReplyLikesPK;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.todayexpression.entity.UsefulExpression;

public class EntityCreator {
        private static final Long QUESTION_ID = 1L;
        private static final Long MEMBER_ID = 1L;
        private static final Long REPLY_ID = 1L;

        public static Question createQuestionEntity() {
                return Question.builder()
                                .questionId(QUESTION_ID)
                                .author(EntityCreator.createMemberEntity())
                                .category("카테고리")
                                .content("This is english content.")
                                .koContent("이것은 한글 내용입니다.")
                                .regDate(LocalDateTime.parse("2020-10-14T11:00:00"))
                                .wordHints(new HashSet<>(List.of(
                                                WordHint.builder()
                                                                .hintId(1L)
                                                                .question(Question.builder().build())
                                                                .word("apple")
                                                                .meaning("사과")
                                                                .regDate(LocalDateTime.parse("2020-10-14T11:00:00"))
                                                                .build())))
                                .build();
        }

        public static Reply createReplyEntity() {
                return Reply.builder()
                                .replyId(REPLY_ID)
                                .author(createMemberEntity())
                                .question(createQuestionEntity())
                                .content("Test Reply")
                                .image("image-path")
                                .replyLikes(new HashSet<>(
                                                Arrays.asList(createReplyLikesEntity(), createReplyLikesEntity())))
                                .build();
        }

        public static Member createMemberEntity() {
                return Member.builder()
                                .memberId(MEMBER_ID)
                                .identifier("123")
                                .email("test@gmail.com")
                                .image("test-image")
                                .isActive(true)
                                .nickname("Test")
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .modDate(LocalDateTime.of(2020, 10, 14, 17, 11, 10))
                                .build();
        }

        public static ReplyLikes createReplyLikesEntity() {
                return ReplyLikes.builder()
                                .id(ReplyLikesPK.builder().replyId(1L).memberId(1L).build())
                                .member(createMemberEntity())
                                .reply(Reply.builder().replyId(1L).build())
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .build();
        }

        public static WordHint createWordHint() {
                return WordHint.builder()
                                .hintId(1L)
                                .question(Question.builder().build())
                                .word("apple")
                                .meaning("사과")
                                .regDate(LocalDateTime.parse("2020-10-14T11:00:00"))
                                .build();
        }

        public static UsefulExpression createUsefulExpressionEntity() {
                return UsefulExpression.builder()
                                .expId(1L)
                                .expression("test")
                                .meaning("테스트")
                                .expressionExample("test driven development")
                                .expressionExampleMeaning("테스트 주도 개발")
                                .memberScraps(new HashSet<>(Arrays.asList(EntityCreator.createMemberScrapEntity(),
                                                EntityCreator.createMemberScrapEntity())))
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .modDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .build();
        }

        public static MemberScrap createMemberScrapEntity() {
                return MemberScrap.builder()
                                .id(new MemberScrapPK(1L, 1L))
                                .member(EntityCreator.createMemberEntity())
                                .usefulExpression(UsefulExpression.builder()
                                                .expId(1L)
                                                .expression("test")
                                                .meaning("테스트")
                                                .expressionExample("test driven development")
                                                .expressionExampleMeaning("테스트 주도 개발")
                                                .memberScraps(new HashSet<>(
                                                                Arrays.asList(
                                                                                MemberScrap.builder()
                                                                                                .id(new MemberScrapPK(
                                                                                                                1L, 1L))
                                                                                                .member(EntityCreator
                                                                                                                .createMemberEntity())
                                                                                                .build())))
                                                .build())
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .build();
        }

        public static Badge createBadgeEntity() {
                return Badge.builder()
                                .badgeId(1L)
                                .name("Badge Test")
                                .content("Badge Test Content")
                                .build();
        }

        public static BadgeAccomplished createBadgeAccomplishedEntity() {
                return BadgeAccomplished.builder()
                                .id(new BadgeAccomplishedPK(1L, 1L))
                                .member(EntityCreator.createMemberEntity())
                                .badge(EntityCreator.createBadgeEntity())
                                .build();
        }

        public static ReplyReport createReplyReportEntity() {
                return ReplyReport.builder()
                                .reportId(1L)
                                .reply(EntityCreator.createReplyEntity())
                                .reporter(EntityCreator.createMemberEntity())
                                .reason("Test Reason")
                                .hideToReporter(true)
                                .status(ReplyReport.ProcessStatus.OPEN)
                                .method(ReplyReport.ProcessMethod.PENDING)
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .build();
        }

        public static MemberReport createMemberReportEntity() {
                Member target = EntityCreator.createMemberEntity();
                target.setMemberId(2L);
                return MemberReport.builder()
                                .reportId(1L)
                                .reporter(EntityCreator.createMemberEntity())
                                .target(target)
                                .reason("Test Reason")
                                .hideToReporter(true)
                                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                                .build();
        }
}
