package net.ink.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.ink.admin.entity.AdminMember;

public interface AdminMemberRepository extends JpaRepository<AdminMember, Long> {
    Optional<AdminMember> findByEmail(String email);

    Optional<AdminMember> findByEmailAndAdminRankNot(String email, AdminMember.RANK rank);

    boolean existsByEmailAndIsActive(String username, Boolean active);

    boolean existsByNicknameAndIsActive(String nickname, Boolean active);
}
