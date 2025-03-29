package net.ink.admin.service;

import lombok.RequiredArgsConstructor;
import net.ink.admin.dto.AdminLogDto;
import net.ink.admin.entity.AdminLog;
import net.ink.admin.repository.AdminLogRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLogService {
    private final AdminLogRepository adminLogRepository;
    private static final int PAGE_THRESHOLD = 50;

    @Transactional
    public void insertLog(AdminLog adminLog) {
        adminLogRepository.save(adminLog);
    }

    /**
     * 조건에 따라 로그 조회 (페이징 자동 활성화)
     */
    @Transactional(readOnly = true)
    public Object getLogs(String email, String action, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        List<AdminLog> logs;
        if (email != null && action != null) {
            logs = adminLogRepository.findByActionedAdminMember_EmailAndAction(email, action);
        } else if (email != null) {
            logs = adminLogRepository.findByActionedAdminMember_Email(email);
        } else if (action != null) {
            logs = adminLogRepository.findByAction(action);
        } else if (fromDate != null && toDate != null) {
            logs = adminLogRepository.findByRegDateBetween(fromDate, toDate);
        } else {
            logs = adminLogRepository.findAll();
        }
        return handlePaging(logs, pageable);
    }


    /**
     * 페이징 처리 여부를 결정
     */
    private Object handlePaging(List<AdminLog> logs, Pageable pageable) {
        if (logs.size() >= PAGE_THRESHOLD) {
            int start = Math.min((int) pageable.getOffset(), logs.size());
            int end = Math.min((start + pageable.getPageSize()), logs.size());
            return new PageImpl<>(
                    logs.subList(start, end).stream()
                            .map(this::convertToDto) // DTO 변환
                            .collect(Collectors.toList()),
                    pageable,
                    logs.size()
            );
        }
        return logs.stream()
                .map(this::convertToDto) // DTO 변환
                .collect(Collectors.toList());
    }

    /**
     * 엔티티를 DTO로 변환
     */
    private AdminLogDto convertToDto(AdminLog adminLog) {
        return AdminLogDto.builder()
                .adminId(adminLog.getAdminId())
                .adminEmail(adminLog.getActionedAdminMember().getEmail())
                .action(adminLog.getAction())
                .actionQuery(adminLog.getActionQuery())
                .regDate(adminLog.getRegDate())
                .build();
    }

}
