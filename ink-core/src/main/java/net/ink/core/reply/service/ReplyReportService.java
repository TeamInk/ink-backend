package net.ink.core.reply.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.member.repository.MemberReportRepository;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.service.event.ReplyReportEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyReportService {
    private final ApplicationEventPublisher eventPublisher;
    private final ReplyReportRepository replyReportRepository;

    @Transactional
    public ReplyReport reportReply(ReplyReport replyReport) {
        eventPublisher.publishEvent(new ReplyReportEvent(replyReport));
        return replyReportRepository.save(replyReport);
    }
}
