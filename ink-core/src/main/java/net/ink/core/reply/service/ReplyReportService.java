package net.ink.core.reply.service;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import net.ink.core.reply.service.event.ReplyReportEvent;

@Service
@RequiredArgsConstructor
public class ReplyReportService {
    private final ApplicationEventPublisher eventPublisher;
    private final ReplyReportRepository replyReportRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public ReplyReport reportReply(ReplyReport replyReport) {
        eventPublisher.publishEvent(new ReplyReportEvent(replyReport));
        return replyReportRepository.save(replyReport);
    }

    @Transactional
    public ReplyReport processReplyReport(Long reportId, ReplyReport.ProcessMethod method, String processBy) {
        ReplyReport report = replyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id: " + reportId));

        // 처리 내용에 따른 게시글 상태 변경
        Reply reply = report.getReply();
        switch (method) {
            case CANCELED:
                // 신고취소 - 아무 동작 안함
                break;
            case HIDED:
                // 게시글 숨김 - visible을 false로 변경
                reply.setVisible(false);
                replyRepository.save(reply);
                break;
            case DELETED:
                // 게시글 삭제 - deleted를 true로 변경
                reply.setDeleted(true);
                replyRepository.save(reply);
                break;
        }

        // 공통적으로 처리되면 status를 DONE으로 변경
        report.setMethod(method);
        report.setStatus(ReplyReport.ProcessStatus.DONE);
        report.setProcessDate(LocalDateTime.now());
        report.setProcessBy(processBy);

        return replyReportRepository.save(report);
    }
}
