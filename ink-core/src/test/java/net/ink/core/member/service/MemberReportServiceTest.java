package net.ink.core.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.ink.core.common.EntityCreator;
import net.ink.core.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import net.ink.core.core.exception.BadRequestException;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.repository.MemberReportRepository;

@ExtendWith(MockitoExtension.class)
class MemberReportServiceTest {
    @InjectMocks
    MemberReportService memberReportService;

    @Mock
    MemberReportRepository memberReportRepository;

    @Test
    @DisplayName("회원 신고 테스트")
    void reportMember() {
        Member reporter = EntityCreator.createMemberEntity();

        Member target = EntityCreator.createMemberEntity();
        target.setMemberId(2L);

        MemberReport memberReport = new MemberReport();
        memberReport.setReporter(reporter);
        memberReport.setTarget(target);

        when(memberReportRepository.save(any(MemberReport.class))).thenReturn(memberReport);

        MemberReport savedReport = memberReportService.reportMember(memberReport);
        assertNotNull(savedReport);
    }

    @Test
    @DisplayName("자기 자신을 신고할 수 없음 테스트")
    void reportSelfWillFail() {
        Member reporter = EntityCreator.createMemberEntity();

        MemberReport memberReport = new MemberReport();
        memberReport.setReporter(reporter);
        memberReport.setTarget(reporter);

        assertThrows(BadRequestException.class, () -> memberReportService.reportMember(memberReport));
    }
}