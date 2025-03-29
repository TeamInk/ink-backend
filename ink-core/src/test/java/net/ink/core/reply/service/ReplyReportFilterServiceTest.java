package net.ink.core.reply.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.ink.core.common.EntityCreator;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberReportRepository;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;

@ExtendWith(MockitoExtension.class)
class ReplyReportFilterServiceTest {

    @Mock
    private ReplyReportRepository replyReportRepository;

    @Mock
    private MemberReportRepository memberReportRepository;

    @InjectMocks
    private ReplyReportFilterService replyReportFilterService;

    private final Reply reply = EntityCreator.createReplyEntity();
    private final Member reporter = EntityCreator.createMemberEntity();
    private final ReplyReport replyReport = EntityCreator.createReplyReportEntity();

    @Test
    @DisplayName("Should return true when reply is hidden by reporter")
    void shouldReturnTrueWhenReplyIsHiddenByReporter() {
        when(memberReportRepository.existsByTargetAndReporterAndHideToReporter(any(), any(), any())).thenReturn(false);
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.singletonList(replyReport));

        assertTrue(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }

    @Test
    @DisplayName("Should return false when reply is not hidden by reporter")
    void shouldReturnFalseWhenReplyIsNotHiddenByReporter() {
        replyReport.setHideToReporter(false);
        when(memberReportRepository.existsByTargetAndReporterAndHideToReporter(any(), any(), any())).thenReturn(false);
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.singletonList(replyReport));

        assertFalse(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }

    @Test
    @DisplayName("Should return true when author was reported for reply")
    void shouldReturnTrueWhenAuthorWasReportedForReply() {
        replyReport.setHideToReporter(false);
        when(memberReportRepository.existsByTargetAndReporterAndHideToReporter(any(), any(), any())).thenReturn(true);

        assertTrue(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }

    @Test
    @DisplayName("Should return false when no reports found for reply")
    void shouldReturnFalseWhenNoReportsFoundForReply() {
        when(memberReportRepository.existsByTargetAndReporterAndHideToReporter(any(), any(), any())).thenReturn(false);
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.emptyList());

        assertFalse(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }
}