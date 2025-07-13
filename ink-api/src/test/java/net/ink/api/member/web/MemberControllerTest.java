package net.ink.api.member.web;

import net.ink.api.annotation.WithMockInkUser;
import net.ink.api.web.AbstractControllerTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends AbstractControllerTest {
        private final LocalDate todayDate = Instant.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
        private final String todayDateString = todayDate
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA));

        @Test
        public void 사용자_회원가입_테스트() throws Exception {
                String newMemberJsonString = "{" +
                                "  \"email\": \"example@email.com\",\n" +
                                "  \"identifier\": \"kakao_1234567890\",\n" +
                                "  \"image\": \"image-path\",\n" +
                                "  \"nickname\": \"테스트닉네임\",\n" +
                                "  \"pushActive\": true\n" +
                                "}";

                mockMvc.perform(
                                post("/api/member/signup")
                                                .content(newMemberJsonString)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(4L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("example@email.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier")
                                                .value("kakao_1234567890"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("image-path"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("테스트닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        public void 사용자_회원가입_테스트_이메일_없음() throws Exception {
                String newMemberJsonString = "{" +
                                "  \"identifier\": \"kakao_1234567890\",\n" +
                                "  \"image\": \"image-path\",\n" +
                                "  \"nickname\": \"테스트닉네임\",\n" +
                                "  \"pushActive\": true\n" +
                                "}";

                mockMvc.perform(
                                post("/api/member/signup")
                                                .content(newMemberJsonString)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(4L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").doesNotExist())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier")
                                                .value("kakao_1234567890"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("image-path"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("테스트닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        public void 사용자_회원가입_닉네임초과_테스트() throws Exception {
                String newMemberJsonStringOverNickname = "{" +
                                "  \"email\": \"example@email.com\",\n" +
                                "  \"identifier\": \"kakao_1234567890\",\n" +
                                "  \"image\": \"image-path\",\n" +
                                "  \"nickname\": \"매우긴닉네임테스트\",\n" +
                                "  \"pushActive\": true\n" +
                                "}";

                mockMvc.perform(
                                post("/api/member/signup")
                                                .content(newMemberJsonStringOverNickname)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(4L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("example@email.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier")
                                                .value("kakao_1234567890"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("image-path"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("매우긴닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        public void 사용자_닉네임중복체크_테스트() throws Exception {
                mockMvc.perform(
                                get("/api/member/nickname-exists/테스트이름")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.duplicated").value(false));
        }

        @Test
        public void 사용자_닉네임중복체크_테스트2() throws Exception {
                mockMvc.perform(
                                get("/api/member/nickname-exists/테스트유저")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.duplicated").value(true));
        }

        @Test
        @WithMockInkUser
        public void 사용자_정보수정_테스트() throws Exception {
                mockMvc.perform(
                                patch("/api/member/modify")
                                                .content("{\"nickname\": \"변경닉네임\", \"image\": \"modified-image-path\"}")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image").value("modified-image-path"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("변경닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        @WithMockInkUser
        public void 사용자_정보_닉네임수정_테스트() throws Exception {
                mockMvc.perform(
                                patch("/api/member/modify")
                                                .content("{\"nickname\": \"변경닉네임\"}")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                                                .value("/reply/1621586761110.png"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("변경닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        @WithMockInkUser
        public void 사용자_정보_닉네임수정_닉네임초과_테스트() throws Exception {
                mockMvc.perform(
                                patch("/api/member/modify")
                                                .content("{\"nickname\": \"매우긴닉네임테스트\"}")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                                                .value("/reply/1621586761110.png"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("매우긴닉네임"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true));
        }

        @Test
        @WithMockInkUser
        public void 로그인한_사용자_가져오기_테스트() throws Exception {
                mockMvc.perform(
                                get("/api/member/me")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                                                .value("/reply/1621586761110.png"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("테스트유저"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true))
                                .andDo(print());
        }

        @Test
        @Disabled("해외에서 타임존 문제 - h2에 저장하고 꺼내올 때 1일 전으로 가져와진다..")
        @WithMockInkUser
        public void 출석체크_테스트() throws Exception {
                // 두 번 호출 했어도 attendanceCount는 같아야 한다.
                mockMvc.perform(
                                get("/api/member/me")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                mockMvc.perform(
                                get("/api/member/me")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                                                .value("/reply/1621586761110.png"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate")
                                                .value(todayDateString))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("테스트유저"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true))
                                .andDo(print());
        }

        @Test
        @WithMockInkUser
        public void 사용자_프로필_가져오기_테스트() throws Exception {
                mockMvc.perform(
                                get("/api/member/{memberId}", 1)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.identifier").value("identity"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.image")
                                                .value("/reply/1621586761110.png"))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastAttendanceDate").value(todayDateString))
                                // .andExpect(MockMvcResultMatchers.jsonPath("$.data.attendanceCount").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.inkCount").value(0))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("테스트유저"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.likePushActive")
                                                .value(true))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberSetting.dailyPushActive")
                                                .value(true))
                                .andDo(print());

        }

        @Test
        @WithMockInkUser
        public void 사용자_탈퇴_테스트() throws Exception {
                mockMvc.perform(
                                delete("/api/member/me")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ok"))
                                .andDo(print());
        }
}