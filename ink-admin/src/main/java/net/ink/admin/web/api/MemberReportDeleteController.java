package net.ink.admin.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.member.repository.MemberReportRepository;

@RestController
@RequiredArgsConstructor
public class MemberReportDeleteController {
    private final MemberReportRepository memberReportRepository;

    @AdminLogging
    @DeleteMapping("/api/member-report/{reportId}")
    public ResponseEntity<?> deleteMemberReport(@PathVariable Long reportId) {
        memberReportRepository.deleteById(reportId);
        return ResponseEntity.ok().build();
    }
}
