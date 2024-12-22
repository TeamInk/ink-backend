package net.ink.core.reply.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import net.ink.push.service.FcmLikePushServiceImpl;
import net.ink.push.service.FcmService;

@SpringBootTest
@Import({FcmLikePushServiceImpl.class, FcmService.class})
class ReplyReportServiceTest {
    @MockBean
    private ReplyRepository replyRepository;

    @MockBean
    private ReplyReportRepository replyReportRepository;

    @Autowired
    private ReplyReportService replyReportService;

    @Test
    public void 답변_신고_테스트() {
        // Given
        Reply reply = new Reply();
        ReplyReport replyReport = new ReplyReport();
        replyReport.setReply(reply);

        when(replyReportRepository.countByReply(any(Reply.class))).thenReturn(2);

        // When
        replyReportService.reportReply(replyReport);

        // Then
        verify(replyRepository, times(0)).save(any(Reply.class));
        verify(replyReportRepository, times(1)).save(any(ReplyReport.class));
    }

    @Test
    public void 답변_신고_3회누적_삭제처리_테스트() {
        // Given
        Reply reply = new Reply();
        ReplyReport replyReport = new ReplyReport();
        replyReport.setReply(reply);

        when(replyReportRepository.countByReply(any(Reply.class))).thenReturn(3);

        // When
        replyReportService.reportReply(replyReport);

        // Then
        verify(replyRepository, times(1)).save(any(Reply.class));
        verify(replyReportRepository, times(1)).save(any(ReplyReport.class));
    }
}