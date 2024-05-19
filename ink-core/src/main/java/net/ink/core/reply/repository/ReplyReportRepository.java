package net.ink.core.reply.repository;

import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    List<ReplyReport> findByReply(Reply reply);
    int countByReply(Reply reply);
}