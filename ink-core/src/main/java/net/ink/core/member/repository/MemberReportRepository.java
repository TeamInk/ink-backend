package net.ink.core.member.repository;

import net.ink.core.member.entity.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    // Add any custom queries if needed
}