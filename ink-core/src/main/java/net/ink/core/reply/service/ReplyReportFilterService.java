package net.ink.core.reply.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberReportRepository;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;

@Service
@RequiredArgsConstructor
public class ReplyReportFilterService {
    private final ReplyReportRepository replyReportRepository;
    private final MemberReportRepository memberReportRepository;

    @Transactional(readOnly = true)
    public boolean isReplyHideByReporter(Reply reply, Member reporter) {
        if (memberReportRepository.existsByTargetAndReporterAndHideToReporter(reply.getAuthor(), reporter, true)) {
            return true;
        }

        List<ReplyReport> replyReports = replyReportRepository.findByReply(reply);
        for (ReplyReport replyReport : replyReports) {
            if (replyReport.getReporter().getMemberId().equals(reporter.getMemberId()) && replyReport.getHideToReporter()) {
                return true;
            }
        }

        return false;
    }
}
