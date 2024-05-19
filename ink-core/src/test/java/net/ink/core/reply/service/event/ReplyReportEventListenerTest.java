package net.ink.core.reply.service.event;

import static org.junit.jupiter.api.Assertions.*;

import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import net.ink.push.service.FcmLikePushServiceImpl;
import net.ink.push.service.FcmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;

@SpringBootTest
@Import({FcmLikePushServiceImpl.class, FcmService.class})
public class ReplyReportEventListenerTest {

    @MockBean
    private ReplyRepository replyRepository;

    @MockBean
    private ReplyReportRepository replyReportRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EntityManager entityManager;

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