package net.ink.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.ink.admin.entity.AdminLog;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
