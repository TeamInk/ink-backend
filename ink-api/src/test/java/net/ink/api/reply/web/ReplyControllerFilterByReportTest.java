package net.ink.api.reply.web;

import net.ink.api.annotation.WithMockInkUser;
import net.ink.api.web.AbstractControllerTest;
import net.ink.core.reply.service.ReplyReportFilterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReplyControllerFilterByReportTest extends AbstractControllerTest {
    @MockBean
    private ReplyReportFilterService replyReportFilterService;

    @Test
    @WithMockInkUser
    public void 답변_인기순_조회_신고_필터_테스트() throws Exception {
        when(replyReportFilterService.isReplyHideByReporter(any(), any()))
                .thenReturn(false)
                .thenReturn(true);

        int page = 0;
        int size = 2;
        mockMvc.perform(
                        get("/api/reply?page={page}&size={size}&sort=popular", page, size)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].replyId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].content").value("this is test reply 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].replyLikeCount").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].question.content").value("This is test question."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].author.nickname").value("테스트유저"))
                .andDo(print());
    }
}
