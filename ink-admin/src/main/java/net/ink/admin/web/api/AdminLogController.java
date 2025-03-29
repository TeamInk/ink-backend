package net.ink.admin.web.api;

import lombok.RequiredArgsConstructor;
import net.ink.admin.service.AdminLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AdminLogController {
    private final AdminLogService adminLogService;

    /**
     * 전체 또는 조건별 로그 조회
     * 페이징은 로그 개수가 일정 개수 이상일 때만 작동
     */
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    @GetMapping("/api/log/admin-logs")
    public ResponseEntity<?> getLogs(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Pageable pageable // 페이징 정보는 항상 파라미터로 받음
    ) {
        // 서비스에서 페이징 및 비페이징 처리를 결정
        Object logs = adminLogService.getLogs(email, action, fromDate, toDate, pageable);
        return ResponseEntity.ok(logs);
    }
}
