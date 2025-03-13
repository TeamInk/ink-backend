package net.ink.admin.entity;

import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberReportTest {

    @Test
    @DisplayName("MemberReport 생성 시 기본 상태는 PENDING이어야 함")
    public void testDefaultProcessStatus() {
        // given
        Member reporter = Member.builder().memberId(1L).nickname("reporter").build();
        Member target = Member.builder().memberId(2L).nickname("target").build();

        // when
        MemberReport report = MemberReport.builder()
                .reporter(reporter)
                .target(target)
                .reason("Test reason")
                .build();

        // then
        assertThat(report.getStatus()).isEqualTo(MemberReport.ProcessStatus.PENDING);
    }

    @Test
    @DisplayName("MemberReport 상태 변경 테스트")
    public void testChangeProcessStatus() {
        // given
        Member reporter = Member.builder().memberId(1L).nickname("reporter").build();
        Member target = Member.builder().memberId(2L).nickname("target").build();
        MemberReport report = MemberReport.builder()
                .reporter(reporter)
                .target(target)
                .reason("Test reason")
                .build();

        // when
        report.setStatus(MemberReport.ProcessStatus.HIDED);
        report.setProcessDate(LocalDateTime.now());
        report.setProcessBy("admin");

        // then
        assertThat(report.getStatus()).isEqualTo(MemberReport.ProcessStatus.HIDED);
        assertThat(report.getProcessDate()).isNotNull();
        assertThat(report.getProcessBy()).isEqualTo("admin");
    }

    @Test
    @DisplayName("MemberReport ProcessStatus 열거형 값 테스트")
    public void testProcessStatusValues() {
        // given & when & then
        assertThat(MemberReport.ProcessStatus.values()).contains(
                MemberReport.ProcessStatus.PENDING,
                MemberReport.ProcessStatus.CANCELED,
                MemberReport.ProcessStatus.HIDED,
                MemberReport.ProcessStatus.DELETED);
    }
}