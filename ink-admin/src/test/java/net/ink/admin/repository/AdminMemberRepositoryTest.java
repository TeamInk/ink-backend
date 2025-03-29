package net.ink.admin.repository;

import static net.ink.admin.entity.AdminMember.RANK.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import net.ink.admin.entity.AdminMember;
import net.ink.core.annotation.InkDataTest;

@InkDataTest
class AdminMemberRepositoryTest {
    @Autowired
    AdminMemberRepository adminMemberRepository;

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/admin_member.xml",
    })
    void findByUsername() {
        Optional<AdminMember> result = adminMemberRepository.findByEmail("admin1@exmaple.com");
        assertTrue(result.isPresent());
        assertEquals("Admin One", result.get().getNickname());
        assertEquals(SUPERVISOR, result.get().getAdminRank());
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/admin_member.xml",
    })
    void findByEmailAndRankNot() {
        Optional<AdminMember> result = adminMemberRepository.findByEmailAndAdminRankNot("admin1@exmaple.com",
                SUPERVISOR);
        assertFalse(result.isPresent());
        result = adminMemberRepository.findByEmailAndAdminRankNot("admin1@exmaple.com", PENDING);
        assertTrue(result.isPresent());
        assertEquals("Admin One", result.get().getNickname());
        assertEquals(SUPERVISOR, result.get().getAdminRank());
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/admin_member.xml",
    })
    void existsByEmailAndIsActive() {
        assertTrue(adminMemberRepository.existsByEmailAndIsActive("admin1@exmaple.com", true));
        assertFalse(adminMemberRepository.existsByEmailAndIsActive("admin1@exmaple.com", false));
        assertFalse(adminMemberRepository.existsByEmailAndIsActive("nonexistent@exmaple.com", true));
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/admin_member.xml",
    })
    void existsByNicknameAndIsActive() {
        assertTrue(adminMemberRepository.existsByNicknameAndIsActive("Admin One", true));
        assertFalse(adminMemberRepository.existsByNicknameAndIsActive("Admin One", false));
        assertFalse(adminMemberRepository.existsByNicknameAndIsActive("Nonexistent", true));
    }
}