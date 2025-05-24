package net.ink.admin.web.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.admin.service.ReplyReportService;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import org.springframework.beans.factory.annotation.Qualifier;

@RestController
@RequiredArgsConstructor
public class ReplyReportStatusController {
    @Qualifier("adminReplyReportService")
    private final ReplyReportService replyReportService;

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

        replyReportService.updateReportStatus(reportId, request.getStatus(), userDetails);
        return ResponseEntity.ok()
                .header("Location", "/reply-report-management")
                .build();
    }
}