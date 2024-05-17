package net.ink.api.reply.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ink.api.annotation.WithMockInkUser;
import net.ink.api.common.DtoCreator;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.api.web.AbstractControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReplyReportControllerTest extends AbstractControllerTest {
    @Test
    @WithMockInkUser
    @DisplayName("Should return OK when reporting a reply")
    public void shouldReturnOkWhenReportingReply() throws Exception {
        ReplyReportDto replyReportDto = DtoCreator.createReplyReportDto();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(replyReportDto);

        mockMvc.perform(post("/api/reply/report")
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
    @DisplayName("Should return BAD REQUEST when reporting a reply fails")
    public void shouldReturnBadRequestWhenReportingReplyFails() throws Exception {
        mockMvc.perform(post("/api/reply/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}