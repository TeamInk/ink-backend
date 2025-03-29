package net.ink.core.reply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    List<ReplyReport> findByReply(Reply reply);
    int countByReply(Reply reply);
}