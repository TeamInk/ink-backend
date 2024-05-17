package net.ink.core.reply.service;

import static org.junit.jupiter.api.Assertions.*;

import net.ink.core.common.EntityCreator;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyReportFilterServiceTest {

    @Mock
    private ReplyReportRepository replyReportRepository;

    @InjectMocks
    private ReplyReportFilterService replyReportFilterService;

    private final Reply reply = EntityCreator.createReplyEntity();
    private final Member reporter = EntityCreator.createMemberEntity();
    private final ReplyReport replyReport = EntityCreator.createReplyReportEntity();

    @Test
    @DisplayName("Should return true when reply is hidden by reporter")
    void shouldReturnTrueWhenReplyIsHiddenByReporter() {
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.singletonList(replyReport));

        assertTrue(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }

    @Test
    @DisplayName("Should return false when reply is not hidden by reporter")
    void shouldReturnFalseWhenReplyIsNotHiddenByReporter() {
        replyReport.setHideToReporter(false);
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.singletonList(replyReport));

        assertFalse(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }

    @Test
    @DisplayName("Should return false when no reports found for reply")
    void shouldReturnFalseWhenNoReportsFoundForReply() {
        when(replyReportRepository.findByReply(reply)).thenReturn(Collections.emptyList());

        assertFalse(replyReportFilterService.isReplyHideByReporter(reply, reporter));
    }
}