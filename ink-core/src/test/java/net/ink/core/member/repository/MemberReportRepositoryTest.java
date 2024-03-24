package net.ink.core.member.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import net.ink.core.annotation.InkDataTest;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@InkDataTest
@DatabaseSetup({
        "classpath:dbunit/entity/member.xml",
        "classpath:dbunit/entity/member_report.xml"})
public class MemberReportRepositoryTest {

    @Autowired
    MemberReportRepository memberReportRepository;

    @Test
    public void 멤버_리포트_조회_테스트() {
        MemberReport memberReport = memberReportRepository.findById(1L).orElseThrow();

        assertEquals(1L, memberReport.getReporter().getMemberId());
        assertEquals(2L, memberReport.getTarget().getMemberId());
        assertEquals("Reason 1", memberReport.getReason());
        assertTrue(memberReport.getHideToReporter());
    }

    @Test
    @ExpectedDatabase(value = "classpath:dbunit/expected/member_report_after_insert.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 멤버_리포트_추가_테스트() {
        MemberReport memberReport = MemberReport.builder()
                .reportId(6L)
                .reporter(Member.builder().memberId(1L).build())
                .target(Member.builder().memberId(2L).build())
                .reason("New Reason")
                .hideToReporter(true)
                .build();

        memberReportRepository.save(memberReport);
    }

    @Test
    @ExpectedDatabase(value = "classpath:dbunit/expected/member_report_after_delete.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 멤버_리포트_삭제_테스트() {
        memberReportRepository.deleteById(1L);
        memberReportRepository.flush();
    }
}