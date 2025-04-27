package net.ink.admin.web.api;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReplyReportDeleteController {
    private final ReplyReportRepository replyReportRepository;

    @AdminLogging
    @DeleteMapping("/api/reply-report/{reportId}")
    public ResponseEntity<?> deleteReplyReport(@PathVariable Long reportId) {
        try {
            ReplyReport report = replyReportRepository.findById(reportId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));
            
            // 해당 답변을 가져옴
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
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 오류 발생 시 로그 출력
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

