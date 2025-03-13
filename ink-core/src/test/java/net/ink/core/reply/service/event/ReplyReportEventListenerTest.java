
package net.ink.core.reply.service.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import net.ink.push.service.FcmLikePushServiceImpl;
import net.ink.push.service.FcmService;

@SpringBootTest
@Import({FcmLikePushServiceImpl.class, FcmService.class})
public class ReplyReportEventListenerTest {

    @MockBean
    private ReplyRepository replyRepository;

    @MockBean
    private ReplyReportRepository replyReportRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void handleReplyReportEventTest() throws InterruptedException {
        // Given
        Reply reply = new Reply();
        ReplyReport replyReport = new ReplyReport();
        replyReport.setReply(reply);
        ReplyReportEvent event = new ReplyReportEvent(replyReport);

        when(replyReportRepository.countByReply(any(Reply.class))).thenReturn(3);

        // When
        applicationEventPublisher.publishEvent(event);

        // Then
        verify(replyRepository, times(1)).save(any(Reply.class));
    }
}