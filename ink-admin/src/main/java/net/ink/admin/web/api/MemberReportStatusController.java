package net.ink.admin.web.api;

import lombok.RequiredArgsConstructor;
import lombok.Data;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.repository.MemberReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class MemberReportStatusController {
    private final MemberReportRepository memberReportRepository;

    @Data
    public static class StatusUpdateRequest {
        private String status;
    }

    @AdminLogging
    @PutMapping("/api/member-report/{reportId}/status")
    public ResponseEntity<?> updateMemberReportStatus(
            @PathVariable Long reportId,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MemberReport report = memberReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId));

        // 문자열 상태를 ProcessStatus 열거형으로 변환
        MemberReport.ProcessStatus processStatus;
        switch (request.getStatus()) {
            case "신고 접수":
                processStatus = MemberReport.ProcessStatus.PENDING;
                break;
            case "계정 정지":
                processStatus = MemberReport.ProcessStatus.HIDED;
                break;
            case "처리 완료":
                processStatus = MemberReport.ProcessStatus.DELETED;
                break;
            default:
                processStatus = MemberReport.ProcessStatus.PENDING;
        }

        report.setStatus(processStatus);
        report.setProcessDate(LocalDateTime.now());
        report.setProcessBy(userDetails.getUsername());

        memberReportRepository.save(report);

        return ResponseEntity.ok().build();
    }
}