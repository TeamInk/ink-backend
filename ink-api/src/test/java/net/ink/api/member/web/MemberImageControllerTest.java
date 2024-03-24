package net.ink.api.member.web;

import net.ink.api.annotation.WithMockInkUser;
import net.ink.api.member.service.MemberImageService;
import net.ink.api.web.AbstractControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberImageControllerTest extends AbstractControllerTest {
    @MockBean
    private MemberImageService memberImageService;

    @Test
    @WithMockInkUser
    public void 이미지_업로드_테스트() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        given(memberImageService.uploadMemberImageFile(any())).willReturn("/image/save/path/reply");

        mockMvc.perform(
                multipart("/api/member/image")
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imagePath").value("/image/save/path/reply"))
                .andDo(print());
    }



    @Test
    @WithMockInkUser
    public void 대형_이미지_업로드_테스트() throws Exception {
        File testImageFile = new File("src/test/resources/testImage.JPG");
        try (FileInputStream input = new FileInputStream(testImageFile)) {
            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    "image",
                    "testImage.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    input
            );
            given(memberImageService.uploadMemberImageFile(any())).willReturn("/image/save/path/reply");

            mockMvc.perform(
                    multipart("/api/member/image")
                            .file(mockMultipartFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
            ).andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data.imagePath").value("/image/save/path/reply"))
                    .andDo(print());
        }
    }
}