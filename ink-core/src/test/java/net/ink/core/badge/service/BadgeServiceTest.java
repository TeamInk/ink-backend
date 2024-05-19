package net.ink.core.badge.service;

import static org.junit.jupiter.api.Assertions.*;

import net.ink.core.badge.entity.Badge;
import net.ink.core.badge.repository.BadgeRepository;
import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadgeServiceTest {

    @InjectMocks
    private BadgeService badgeService;

    @Mock
    private BadgeRepository badgeRepository;

    @Test
    @DisplayName("findById returns badge when badge exists")
    public void findByIdReturnsBadgeWhenBadgeExists() {
        Badge badge = EntityCreator.createBadgeEntity();
        when(badgeRepository.findById(anyLong())).thenReturn(Optional.of(badge));

        Badge result = badgeService.findById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("findById throws EntityNotFoundException when badge does not exist")
    public void findByIdThrowsEntityNotFoundExceptionWhenBadgeDoesNotExist() {
        when(badgeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> badgeService.findById(1L));
    }
}