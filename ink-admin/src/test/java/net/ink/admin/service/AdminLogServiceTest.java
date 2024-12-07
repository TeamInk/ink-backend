package net.ink.admin.service;

import net.ink.admin.dto.AdminLogDto;
import net.ink.admin.entity.AdminLog;
import net.ink.admin.entity.AdminMember;
import net.ink.admin.repository.AdminLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminLogServiceTest {

    private final AdminLogRepository adminLogRepository = mock(AdminLogRepository.class);
    private final AdminLogService adminLogService = new AdminLogService(adminLogRepository);

    @Test
    void testGetLogs_NoConditions_ReturnsAllLogsAsDtos() {
        // Arrange
        AdminMember adminMember = AdminMember.builder()
                .email("admin@example.com")
                .build();
        AdminLog log1 = AdminLog.builder()
                .adminId(1L)
                .actionedAdminMember(adminMember)
                .action("ADD_USER")
                .actionQuery("{\"userId\":123}")
                .regDate(LocalDateTime.now())
                .build();
        AdminLog log2 = AdminLog.builder()
                .adminId(2L)
                .actionedAdminMember(adminMember)
                .action("DELETE_USER")
                .actionQuery("{\"userId\":456}")
                .regDate(LocalDateTime.now())
                .build();

        List<AdminLog> logs = Arrays.asList(log1, log2);
        when(adminLogRepository.findAll()).thenReturn(logs);

        // Act
        Object result = adminLogService.getLogs(null, null, null, null, Pageable.unpaged());

        // Assert
        assertThat(result).isInstanceOf(List.class);
        List<AdminLogDto> dtos = (List<AdminLogDto>) result;
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getAdminEmail()).isEqualTo("admin@example.com");
        assertThat(dtos.get(0).getAction()).isEqualTo("ADD_USER");
    }

    @Test
    void testGetLogs_WithPaging_ReturnsPagedResult() {
        // Arrange
        AdminMember adminMember = AdminMember.builder()
                .email("admin@example.com")
                .build();
        AdminLog log = AdminLog.builder()
                .adminId(1L)
                .actionedAdminMember(adminMember)
                .action("ADD_USER")
                .actionQuery("{\"userId\":123}")
                .regDate(LocalDateTime.now())
                .build();

        List<AdminLog> logs = Arrays.asList(log);
        when(adminLogRepository.findAll()).thenReturn(logs);

        // Act
        Object result = adminLogService.getLogs(null, null, null, null, Pageable.ofSize(1));

        // Assert
        assertThat(result).isInstanceOf(PageImpl.class);
        PageImpl<AdminLogDto> page = (PageImpl<AdminLogDto>) result;
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getAdminEmail()).isEqualTo("admin@example.com");
    }
}
