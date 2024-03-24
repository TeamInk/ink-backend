package net.ink.core.reply.repository;

import net.ink.core.member.entity.ReplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    // Add any custom queries if needed
}