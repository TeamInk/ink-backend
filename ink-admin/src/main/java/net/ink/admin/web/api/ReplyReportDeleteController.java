package net.ink.admin.web.api;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.admin.service.ReplyReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReplyReportDeleteController {
    private final ReplyReportService replyReportService;

    @AdminLogging
    @DeleteMapping("/api/reply-report/{reportId}")
    public ResponseEntity<?> deleteReplyReport(@PathVariable Long reportId) {
        replyReportService.cancelReport(reportId);
        return ResponseEntity.ok().build();
    }
}

