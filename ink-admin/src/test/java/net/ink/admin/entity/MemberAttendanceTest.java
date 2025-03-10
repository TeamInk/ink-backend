package net.ink.admin.entity;

import net.ink.core.member.entity.MemberAttendance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberAttendanceTest {

    @Test
    @DisplayName("출석 횟수 증가 메서드 테스트")
    public void testIncreaseAttendanceCount() {
        // given
        MemberAttendance memberAttendance = MemberAttendance.builder()
                .attendanceCount(5)
                .lastAttendanceDate(LocalDate.now().minusDays(1))
                .build();

        int initialCount = memberAttendance.getAttendanceCount();
        LocalDate initialDate = memberAttendance.getLastAttendanceDate();

        // when
        memberAttendance.increaseAttendanceCount();

        // then
        assertThat(memberAttendance.getAttendanceCount()).isEqualTo(initialCount + 1);
        assertThat(memberAttendance.getLastAttendanceDate()).isNotEqualTo(initialDate);
        assertThat(memberAttendance.getLastAttendanceDate()).isEqualTo(LocalDate.now());
    }
}