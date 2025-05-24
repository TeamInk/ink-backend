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

    @Transactional
    public void cancelReport(Long reportId) {
        ReplyReport report = replyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다: " + reportId));
        Reply reply = report.getReply();

        // 게시물 숨김 상태인 경우, 다시 노출시키기 위해 PENDING으로 변경
        if (report.getStatus() == ReplyReport.ProcessStatus.HIDED) {
            List<ReplyReport> allReports = replyReportRepository.findByReply(reply);
            for (ReplyReport r : allReports) {
                if (r.getStatus() == ReplyReport.ProcessStatus.HIDED) {
                    r.setStatus(ReplyReport.ProcessStatus.PENDING);
                    replyReportRepository.save(r);
                }
            }
        }

        // 해당 답변의 모든 신고 기록 삭제
        List<ReplyReport> reportsToDelete = replyReportRepository.findByReply(reply);
        for (ReplyReport r : reportsToDelete) {
            replyReportRepository.delete(r);
        }
    }

    @Transactional
    public ReplyReport updateReportStatus(Long reportId, String status, UserDetails userDetails) {
        ReplyReport report = replyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다: " + reportId));

        // 상태 변경 처리
        ReplyReport.ProcessStatus processStatus;
        switch (status) {
            case "신고 접수":
                processStatus = ReplyReport.ProcessStatus.PENDING;
                break;
            case "게시물 숨김":
                processStatus = ReplyReport.ProcessStatus.HIDED;
                break;
            case "처리 완료":
                processStatus = ReplyReport.ProcessStatus.DELETED;
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 상태입니다: " + status);
        }

        report.setStatus(processStatus);
        report.setProcessDate(LocalDateTime.now());
        report.setProcessBy(userDetails.getUsername());

        return replyReportRepository.save(report);
    }
} 