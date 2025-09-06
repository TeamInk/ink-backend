package net.ink.admin.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("adminReplyReportService")
@RequiredArgsConstructor
public class ReplyReportService {
    private final ReplyReportRepository replyReportRepository;

}