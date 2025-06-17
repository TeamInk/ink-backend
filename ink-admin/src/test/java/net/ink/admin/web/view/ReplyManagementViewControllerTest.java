package net.ink.admin.web.view;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;

@ExtendWith(MockitoExtension.class)
class ReplyManagementViewControllerTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private ReplyReportRepository replyReportRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ReplyManagementViewController controller;

    @Test
    @DisplayName("답변 관리 페이지 호출 테스트")
    void testGetReplyManagement() {
        // given
        given(replyRepository.findAll(any(Sort.class))).willReturn(Collections.emptyList());

        // when
        String viewName = controller.getReplyManagement(model);

        // then
        assertThat(viewName).isEqualTo("base");
        verify(model).addAttribute("inner", "reply-management");
        verify(model).addAttribute(eq("replies"), any());
    }

    @Test
    @DisplayName("답변 신고 관리 페이지 호출 테스트")
    void testGetReplyReportManagement() {
        // given
        given(replyReportRepository.findAll(any(Sort.class))).willReturn(Collections.emptyList());

        // when
        String viewName = controller.getReplyReportManagement(model);

        // then
        assertThat(viewName).isEqualTo("base");
        verify(model).addAttribute("inner", "reply-report-management");
        verify(model).addAttribute(eq("replyReports"), any());
    }

    @Test
    @DisplayName("답변 신고 상세 관리 페이지 호출 테스트")
    void testGetReplyReportManagementDetail() {
        // given
        Long reportId = 1L;
        ReplyReport mockReport = ReplyReport.builder().reportId(reportId).build();
        given(replyReportRepository.findById(reportId)).willReturn(Optional.of(mockReport));

        // when
        String viewName = controller.getReplyReportManagementDetail(reportId, model);

        // then
        assertThat(viewName).isEqualTo("base");
        verify(model).addAttribute("inner", "reply-report-management-detail");
        verify(model).addAttribute(eq("report"), any());
    }
}