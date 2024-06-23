package net.ink.core.badge.service;

import static net.ink.core.core.message.ErrorMessage.NOT_EXIST_BADGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.ink.core.badge.entity.Badge;
import net.ink.core.badge.repository.BadgeRepository;
import net.ink.core.core.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {
    @InjectMocks
    BadgeService badgeService;

    @Mock
    BadgeRepository badgeRepository;

    @Test
    @DisplayName("배지 ID로 찾기 테스트")
    void findById() {
        Badge badge = new Badge();
        badge.setBadgeId(1L);

        when(badgeRepository.findById(anyLong())).thenReturn(Optional.of(badge));

        Badge foundBadge = badgeService.findById(1L);
        assertNotNull(foundBadge);
        assertEquals(1L, foundBadge.getBadgeId());
    }

    @Test
    @DisplayName("존재하지 않는 배지 ID로 찾기 테스트")
    void findByIdNotFound() {
        when(badgeRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> badgeService.findById(1L));
        assertEquals(NOT_EXIST_BADGE, exception.getMessage());
    }
}