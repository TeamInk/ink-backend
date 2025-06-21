package net.ink.admin.web.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ink.admin.web.AbstractControllerTest;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;

@Disabled("테스트 환경 설정 문제로 인해 비활성화")
public class ReplyReportStatusControllerTest extends AbstractControllerTest {

    @Autowired
    private ReplyReportRepository replyReportRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("답변 신고 상태 변경 테스트 - 신고 접수")
    public void testUpdateReplyReportStatusToPending() throws Exception {
        // given
        Long reportId = 1L;
        ReplyReportStatusController.StatusUpdateRequest request = new ReplyReportStatusController.StatusUpdateRequest();
        request.setStatus("신고 접수");

        // when
        mockMvc.perform(put("/api/reply-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/reply-report-management"));

        // then
        ReplyReport report = replyReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(ReplyReport.ProcessStatus.PENDING);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("답변 신고 상태 변경 테스트 - 게시물 숨김")
    public void testUpdateReplyReportStatusToHided() throws Exception {
        // given
        Long reportId = 1L;
        ReplyReportStatusController.StatusUpdateRequest request = new ReplyReportStatusController.StatusUpdateRequest();
        request.setStatus("게시물 숨김");

        // when
        mockMvc.perform(put("/api/reply-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/reply-report-management"));

        // then
        ReplyReport report = replyReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(ReplyReport.ProcessStatus.HIDED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("답변 신고 상태 변경 테스트 - 처리 완료")
    public void testUpdateReplyReportStatusToDeleted() throws Exception {
        // given
        Long reportId = 1L;
        ReplyReportStatusController.StatusUpdateRequest request = new ReplyReportStatusController.StatusUpdateRequest();
        request.setStatus("처리 완료");

        // when
        mockMvc.perform(put("/api/reply-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/reply-report-management"));

        // then
        ReplyReport report = replyReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(ReplyReport.ProcessStatus.DELETED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("답변 신고 상태 변경 테스트 - 신고 취소")
    public void testUpdateReplyReportStatusToCanceled() throws Exception {
        // given
        Long reportId = 1L;
        ReplyReportStatusController.StatusUpdateRequest request = new ReplyReportStatusController.StatusUpdateRequest();
        request.setStatus("신고 취소");

        // when
        mockMvc.perform(put("/api/reply-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/reply-report-management"));

        // then
        ReplyReport report = replyReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(ReplyReport.ProcessStatus.CANCELED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("존재하지 않는 답변 신고 ID로 상태 변경 시 예외 발생 테스트")
    public void testUpdateReplyReportStatusWithNonExistingReportId() throws Exception {
        // given
        Long nonExistingReportId = 999L;
        ReplyReportStatusController.StatusUpdateRequest request = new ReplyReportStatusController.StatusUpdateRequest();
        request.setStatus("신고 접수");

        // when & then
        mockMvc.perform(put("/api/reply-report/{reportId}/status", nonExistingReportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}