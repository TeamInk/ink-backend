package net.ink.api.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ink.api.annotation.WithMockInkUser;
import net.ink.api.common.DtoCreator;
import net.ink.api.member.dto.MemberReportDto;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.api.web.AbstractControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberReportControllerTest extends AbstractControllerTest {
    @Test
    @WithMockInkUser
    @DisplayName("Should return OK when reporting a member")
    public void shouldReturnOkWhenReportingReply() throws Exception {
        MemberReportDto memberReportDto = DtoCreator.createMemberReportDto();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(memberReportDto);

        mockMvc.perform(post("/api/member/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reason").value("Test Reason"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reporterId").value(1))
                .andDo(print());
    }

    @Test
    @WithMockInkUser
    @DisplayName("Should return BAD REQUEST when reporting self")
    public void shouldReturnBadRequestWhenReportingSelf() throws Exception {
        MemberReportDto memberReportDto = DtoCreator.createMemberReportDto();
        memberReportDto.setTargetId(1L);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(memberReportDto);

        mockMvc.perform(post("/api/member/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockInkUser
    @DisplayName("Should return BAD REQUEST when reporting a member fails")
    public void shouldReturnBadRequestWhenReportingReplyFails() throws Exception {
        mockMvc.perform(post("/api/member/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}