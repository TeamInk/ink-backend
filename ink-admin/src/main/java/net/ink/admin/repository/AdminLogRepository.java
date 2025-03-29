package net.ink.admin.repository;

import net.ink.admin.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.ink.admin.entity.AdminLog;

import net.ink.admin.entity.AdminLog;
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    // 특정 관리자 이메일로 로그 조회
    List<AdminLog> findByActionedAdminMember_Email(String email);

    // 특정 액션으로 로그 조회
    List<AdminLog> findByAction(String action);

    // 특정 기간 동안의 로그 조회
    List<AdminLog> findByRegDateBetween(LocalDateTime from, LocalDateTime to);

    // 관리자 이메일 + 액션 조합으로 로그 조회
    List<AdminLog> findByActionedAdminMember_EmailAndAction(String email, String action);

    // 페이징 지원: 특정 액션으로 로그 조회
    Page<AdminLog> findByAction(String action, Pageable pageable);

    // 페이징 지원: 특정 기간 동안의 로그 조회
    Page<AdminLog> findByRegDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
