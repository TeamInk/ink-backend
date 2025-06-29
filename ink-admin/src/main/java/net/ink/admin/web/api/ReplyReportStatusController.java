package net.ink.admin.web.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.service.ReplyReportService;

@RestController
@RequiredArgsConstructor
public class ReplyReportStatusController {
    private final ReplyReportService replyReportService;

    @Data
    public static class MethodUpdateRequest {
        private String method;
    }

    @AdminLogging
    @PutMapping("/api/reply-report/{reportId}/method")
    public ResponseEntity<?> updateReplyReportMethod(
            @PathVariable Long reportId,
            @RequestBody MethodUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 문자열 처리 방법을 ProcessMethod 열거형으로 변환
        ReplyReport.ProcessMethod processMethod;
        switch (request.getMethod()) {
            case "신고취소":
                processMethod = ReplyReport.ProcessMethod.CANCELED;
                break;
            case "게시글 숨김":
                processMethod = ReplyReport.ProcessMethod.HIDED;
                break;
            case "게시글 삭제":
                processMethod = ReplyReport.ProcessMethod.DELETED;
                break;
            default:
                processMethod = ReplyReport.ProcessMethod.PENDING;
        }

        replyReportService.processReplyReport(reportId, processMethod, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }
}