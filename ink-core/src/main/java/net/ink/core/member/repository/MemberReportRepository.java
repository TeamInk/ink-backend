package net.ink.core.member.repository;

import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    // Add any custom queries if needed
    List<MemberReport> findByTarget(Member target);
    boolean existsByTargetAndReporterAndHideToReporter(Member target, Member reporter, Boolean hideToReporter);
}