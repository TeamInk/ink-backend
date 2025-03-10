package net.ink.admin.web.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;

@RestController
@RequiredArgsConstructor
public class ReplyReportStatusController {
    private final ReplyReportRepository replyReportRepository;

    @Data
    public static class StatusUpdateRequest {
        private String status;
    }

    @AdminLogging
    @PutMapping("/api/reply-report/{reportId}/status")
    public ResponseEntity<?> updateReplyReportStatus(
            @PathVariable Long reportId,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReplyReport report = replyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));

        // 문자열 상태를 ProcessStatus 열거형으로 변환
        ReplyReport.ProcessStatus processStatus;
        switch (request.getStatus()) {
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
                processStatus = ReplyReport.ProcessStatus.PENDING;
        }

        report.setStatus(processStatus);
        report.setProcessDate(LocalDateTime.now());
        report.setProcessBy(userDetails.getUsername());

        replyReportRepository.save(report);

        return ResponseEntity.ok().build();
    }
}