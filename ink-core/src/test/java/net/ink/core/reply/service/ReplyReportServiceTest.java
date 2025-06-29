package net.ink.core.reply.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import net.ink.core.member.entity.Member;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;

@ExtendWith(MockitoExtension.class)
class ReplyReportServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ReplyReportRepository replyReportRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReplyReportService replyReportService;

    @Test
    @DisplayName("신고취소 처리 시 답글 상태는 변경되지 않고 신고 상태만 DONE으로 변경된다")
    void processReplyReport_WithCanceledMethod_ShouldNotChangeReplyStatus() {
        // Arrange
        Long reportId = 1L;
        String processBy = "admin@test.com";

        Reply reply = Reply.builder()
                .replyId(1L)
                .visible(true)
                .deleted(false)
                .build();

        ReplyReport report = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.OPEN)
                .method(ReplyReport.ProcessMethod.PENDING)
                .build();

        ReplyReport savedReport = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.DONE)
                .method(ReplyReport.ProcessMethod.CANCELED)
                .processBy(processBy)
                .processDate(LocalDateTime.now())
                .build();

        given(replyReportRepository.findById(reportId)).willReturn(Optional.of(report));
        given(replyReportRepository.save(any(ReplyReport.class))).willReturn(savedReport);

        // Act
        ReplyReport result = replyReportService.processReplyReport(reportId, ReplyReport.ProcessMethod.CANCELED,
                processBy);

        // Assert
        assertEquals(ReplyReport.ProcessStatus.DONE, result.getStatus());
        assertEquals(ReplyReport.ProcessMethod.CANCELED, result.getMethod());
        assertEquals(processBy, result.getProcessBy());
        assertNotNull(result.getProcessDate());

        // Reply 상태는 변경되지 않아야 함
        assertTrue(reply.getVisible());
        assertFalse(reply.getDeleted());

        // Reply 저장은 호출되지 않아야 함
        verify(replyRepository, never()).save(any(Reply.class));
        verify(replyReportRepository, times(1)).save(any(ReplyReport.class));
    }

    @Test
    @DisplayName("게시글 숨김 처리 시 답글의 visible이 false로 변경되고 신고 상태가 DONE으로 변경된다")
    void processReplyReport_WithHidedMethod_ShouldSetVisibleToFalse() {
        // Arrange
        Long reportId = 1L;
        String processBy = "admin@test.com";

        Reply reply = Reply.builder()
                .replyId(1L)
                .visible(true)
                .deleted(false)
                .build();

        ReplyReport report = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.OPEN)
                .method(ReplyReport.ProcessMethod.PENDING)
                .build();

        ReplyReport savedReport = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.DONE)
                .method(ReplyReport.ProcessMethod.HIDED)
                .processBy(processBy)
                .processDate(LocalDateTime.now())
                .build();

        given(replyReportRepository.findById(reportId)).willReturn(Optional.of(report));
        given(replyReportRepository.save(any(ReplyReport.class))).willReturn(savedReport);
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        ReplyReport result = replyReportService.processReplyReport(reportId, ReplyReport.ProcessMethod.HIDED,
                processBy);

        // Assert
        assertEquals(ReplyReport.ProcessStatus.DONE, result.getStatus());
        assertEquals(ReplyReport.ProcessMethod.HIDED, result.getMethod());
        assertEquals(processBy, result.getProcessBy());
        assertNotNull(result.getProcessDate());

        // Reply의 visible이 false로 변경되어야 함
        assertFalse(reply.getVisible());
        assertFalse(reply.getDeleted());

        verify(replyRepository, times(1)).save(reply);
        verify(replyReportRepository, times(1)).save(any(ReplyReport.class));
    }

    @Test
    @DisplayName("게시글 삭제 처리 시 답글의 deleted가 true로 변경되고 신고 상태가 DONE으로 변경된다")
    void processReplyReport_WithDeletedMethod_ShouldSetDeletedToTrue() {
        // Arrange
        Long reportId = 1L;
        String processBy = "admin@test.com";

        Reply reply = Reply.builder()
                .replyId(1L)
                .visible(true)
                .deleted(false)
                .build();

        ReplyReport report = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.OPEN)
                .method(ReplyReport.ProcessMethod.PENDING)
                .build();

        ReplyReport savedReport = ReplyReport.builder()
                .reportId(reportId)
                .reply(reply)
                .status(ReplyReport.ProcessStatus.DONE)
                .method(ReplyReport.ProcessMethod.DELETED)
                .processBy(processBy)
                .processDate(LocalDateTime.now())
                .build();

        given(replyReportRepository.findById(reportId)).willReturn(Optional.of(report));
        given(replyReportRepository.save(any(ReplyReport.class))).willReturn(savedReport);
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        ReplyReport result = replyReportService.processReplyReport(reportId, ReplyReport.ProcessMethod.DELETED,
                processBy);

        // Assert
        assertEquals(ReplyReport.ProcessStatus.DONE, result.getStatus());
        assertEquals(ReplyReport.ProcessMethod.DELETED, result.getMethod());
        assertEquals(processBy, result.getProcessBy());
        assertNotNull(result.getProcessDate());

        // Reply의 deleted가 true로 변경되어야 함
        assertTrue(reply.getVisible());
        assertTrue(reply.getDeleted());

        verify(replyRepository, times(1)).save(reply);
        verify(replyReportRepository, times(1)).save(any(ReplyReport.class));
    }

    @Test
    @DisplayName("존재하지 않는 신고 ID로 처리 시 IllegalArgumentException이 발생한다")
    void processReplyReport_WithInvalidReportId_ShouldThrowException() {
        // Arrange
        Long invalidReportId = 999L;
        String processBy = "admin@test.com";

        given(replyReportRepository.findById(invalidReportId)).willReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> replyReportService.processReplyReport(invalidReportId, ReplyReport.ProcessMethod.CANCELED,
                        processBy));

        assertEquals("Invalid report Id: " + invalidReportId, exception.getMessage());
        verify(replyRepository, never()).save(any(Reply.class));
        verify(replyReportRepository, never()).save(any(ReplyReport.class));
    }

    @Test
    @DisplayName("신고 접수 시 이벤트가 발행되고 신고가 저장된다")
    void reportReply_ShouldPublishEventAndSaveReport() {
        // Arrange
        Member reporter = Member.builder().memberId(1L).build();
        Reply reply = Reply.builder().replyId(1L).build();

        ReplyReport replyReport = ReplyReport.builder()
                .reply(reply)
                .reporter(reporter)
                .reason("부적절한 내용")
                .build();

        ReplyReport savedReport = ReplyReport.builder()
                .reportId(1L)
                .reply(reply)
                .reporter(reporter)
                .reason("부적절한 내용")
                .status(ReplyReport.ProcessStatus.OPEN)
                .method(ReplyReport.ProcessMethod.PENDING)
                .build();

        given(replyReportRepository.save(replyReport)).willReturn(savedReport);

        // Act
        ReplyReport result = replyReportService.reportReply(replyReport);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReportId());
        assertEquals("부적절한 내용", result.getReason());

        verify(eventPublisher, times(1)).publishEvent(any(net.ink.core.reply.service.event.ReplyReportEvent.class));
        verify(replyReportRepository, times(1)).save(replyReport);
    }
}