package net.ink.core.member.service;

import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.repository.MemberReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberReportServiceTest {
    @InjectMocks
    private MemberReportService memberReportService;

    @Mock
    private MemberReportRepository memberReportRepository;

    @Test
    @DisplayName("회원 신고 성공")
    void shouldReportMemberSuccessfully() {
        // Given
        MemberReport memberReport = new MemberReport();
        Member reporter = EntityCreator.createMemberEntity();
        Member target = EntityCreator.createMemberEntity();
        target.setMemberId(2L);
        memberReport.setReporter(reporter);
        memberReport.setTarget(target);

        when(memberReportRepository.save(any(MemberReport.class))).thenReturn(memberReport);

        // When
        MemberReport result = memberReportService.reportMember(memberReport);

        // Then
        assertEquals(memberReport, result);
        verify(memberReportRepository, times(1)).save(memberReport);
    }

    @Test
    @DisplayName("자기 자신을 신고할 수 없음")
    void shouldNotReportSelf() {
        // Given
        MemberReport memberReport = new MemberReport();
        Member reporter = EntityCreator.createMemberEntity();
        reporter.setMemberId(1L);
        memberReport.setReporter(reporter);
        memberReport.setTarget(reporter);

        // When & Then
        assertThrows(BadRequestException.class, () -> memberReportService.reportMember(memberReport));
    }
}