package net.ink.core.reply.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyReportFilterService {
    private final ReplyReportRepository replyReportRepository;

    @Transactional(readOnly = true)
    public boolean isReplyHideByReporter(Reply reply, Member reporter) {
        List<ReplyReport> replyReports = replyReportRepository.findByReply(reply);
        for (ReplyReport replyReport : replyReports) {
            if (replyReport.getReporter().getMemberId().equals(reporter.getMemberId()) && replyReport.getHideToReporter()) {
                return true;
            }
        }
        return false;
    }
}
