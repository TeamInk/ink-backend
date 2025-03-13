package net.ink.admin.web.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ink.admin.web.AbstractControllerTest;

@Disabled("테스트 환경 설정 문제로 인해 비활성화")
public class MemberManagementControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원 신고 관리 페이지 로드 테스트")
    public void testGetMemberReportManagement() throws Exception {
        // given & when & then
        mockMvc.perform(get("/member-report-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("base"))
                .andExpect(model().attributeExists("inner"))
                .andExpect(model().attributeExists("memberReports"))
                .andExpect(model().attribute("inner", "member-report-management"));
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원별 필터링된 답변 관리 페이지 로드 테스트")
    public void testGetFilteredReplyManagement() throws Exception {
        // given
        Long memberId = 1L;

        // when & then
        mockMvc.perform(get("/filtered-reply-management")
                .param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("base"))
                .andExpect(model().attributeExists("inner"))
                .andExpect(model().attributeExists("replies"))
                .andExpect(model().attribute("inner", "reply-management"));
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = { "ADMIN" })
    @DisplayName("회원별 필터링된 답변 관리 페이지 - 잘못된 memberId 파라미터 테스트")
    public void testGetFilteredReplyManagementWithInvalidMemberId() throws Exception {
        // given
        String invalidMemberId = "invalid";

        // when & then
        mockMvc.perform(get("/filtered-reply-management")
                .param("memberId", invalidMemberId))
                .andExpect(status().isBadRequest());
    }
}