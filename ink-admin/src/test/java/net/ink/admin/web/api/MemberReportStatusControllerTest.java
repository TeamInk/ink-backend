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
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.repository.MemberReportRepository;

@Disabled("테스트 환경 설정 문제로 인해 비활성화")
public class MemberReportStatusControllerTest extends AbstractControllerTest {

    @Autowired
    private MemberReportRepository memberReportRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원 신고 상태 변경 테스트 - 신고 접수")
    public void testUpdateMemberReportStatusToPending() throws Exception {
        // given
        Long reportId = 1L;
        MemberReportStatusController.StatusUpdateRequest request = new MemberReportStatusController.StatusUpdateRequest();
        request.setStatus("신고 접수");

        // when
        mockMvc.perform(put("/api/member-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        MemberReport report = memberReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(MemberReport.ProcessStatus.PENDING);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원 신고 상태 변경 테스트 - 계정 정지")
    public void testUpdateMemberReportStatusToHided() throws Exception {
        // given
        Long reportId = 1L;
        MemberReportStatusController.StatusUpdateRequest request = new MemberReportStatusController.StatusUpdateRequest();
        request.setStatus("계정 정지");

        // when
        mockMvc.perform(put("/api/member-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        MemberReport report = memberReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(MemberReport.ProcessStatus.HIDED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원 신고 상태 변경 테스트 - 처리 완료")
    public void testUpdateMemberReportStatusToDeleted() throws Exception {
        // given
        Long reportId = 1L;
        MemberReportStatusController.StatusUpdateRequest request = new MemberReportStatusController.StatusUpdateRequest();
        request.setStatus("처리 완료");

        // when
        mockMvc.perform(put("/api/member-report/{reportId}/status", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        MemberReport report = memberReportRepository.findById(reportId).orElseThrow();
        assertThat(report.getStatus()).isEqualTo(MemberReport.ProcessStatus.DELETED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin@email.com");
    }
}