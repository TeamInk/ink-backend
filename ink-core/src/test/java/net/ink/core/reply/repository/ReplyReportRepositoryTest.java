package net.ink.core.reply.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import net.ink.core.annotation.InkDataTest;
import net.ink.core.member.entity.Member;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;

@InkDataTest
@DatabaseSetup({
        "classpath:dbunit/entity/member.xml",
        "classpath:dbunit/entity/question.xml",
        "classpath:dbunit/entity/reply.xml",
        "classpath:dbunit/entity/reply_likes.xml",
        "classpath:dbunit/entity/reply_report.xml"})
public class ReplyReportRepositoryTest {

    @Autowired
    ReplyReportRepository replyReportRepository;

    @Test
    public void 댓글_리포트_조회_테스트() {
        ReplyReport replyReport = replyReportRepository.findById(1L).orElseThrow();

        assertEquals(1L, replyReport.getReply().getReplyId());
        assertEquals(1L, replyReport.getReporter().getMemberId());
        assertEquals("Reason 1", replyReport.getReason());
        assertTrue(replyReport.getHideToReporter());
    }

    @Test
    @ExpectedDatabase(value = "classpath:dbunit/expected/reply_report_after_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 댓글_리포트_추가_테스트() {
        ReplyReport replyReport = ReplyReport.builder()
                .reportId(6L)
                .reply(Reply.builder().replyId(1L).build())
                .reporter(Member.builder().memberId(1L).build())
                .reason("New Reason")
                .hideToReporter(true)
                .build();

        replyReportRepository.save(replyReport);
    }

    @Test
    @ExpectedDatabase(value = "classpath:dbunit/expected/reply_report_after_delete.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 댓글_리포트_삭제_테스트() {
        replyReportRepository.deleteById(1L);
        replyReportRepository.flush();
    }

    @Test
    public void 댓글로_연관_리포트_조회_테스트() {
        Reply reply = Reply.builder().replyId(1L).build();
        ReplyReport replyReport = replyReportRepository.findByReply(reply).get(0);

        assertEquals(1L, replyReport.getReportId());
        assertEquals(1L, replyReport.getReporter().getMemberId());
        assertEquals("Reason 1", replyReport.getReason());
        assertTrue(replyReport.getHideToReporter());
    }

    @Test
    public void 댓글로_연관_리포트_count_조회_테스트() {
        Reply reply = Reply.builder().replyId(1L).build();
        int count = replyReportRepository.countByReply(reply);

        assertEquals(2, count);
    }
}