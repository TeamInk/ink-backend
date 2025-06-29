package net.ink.admin.web.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;

@ExtendWith(MockitoExtension.class)
class ReplyHideControllerTest {

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private ReplyHideController replyHideController;

    @Test
    @DisplayName("답변 숨김처리 - 성공")
    void hideReply_Success() {
        // Arrange
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .replyId(replyId)
                .visible(true)
                .build();

        given(replyRepository.findById(replyId)).willReturn(Optional.of(reply));
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        ResponseEntity<?> response = replyHideController.hideReply(replyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(replyRepository, times(1)).findById(replyId);
        verify(replyRepository, times(1)).save(any(Reply.class));
    }

    @Test
    @DisplayName("답변 숨김처리 - 존재하지 않는 답변")
    void hideReply_ReplyNotFound() {
        // Arrange
        Long replyId = 999L;
        given(replyRepository.findById(replyId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> replyHideController.hideReply(replyId));

        verify(replyRepository, times(1)).findById(replyId);
        verify(replyRepository, times(0)).save(any(Reply.class));
    }

    @Test
    @DisplayName("답변 숨김해제 - 성공")
    void unhideReply_Success() {
        // Arrange
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .replyId(replyId)
                .visible(false)
                .build();

        given(replyRepository.findById(replyId)).willReturn(Optional.of(reply));
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        ResponseEntity<?> response = replyHideController.unhideReply(replyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(replyRepository, times(1)).findById(replyId);
        verify(replyRepository, times(1)).save(any(Reply.class));
    }

    @Test
    @DisplayName("답변 숨김해제 - 존재하지 않는 답변")
    void unhideReply_ReplyNotFound() {
        // Arrange
        Long replyId = 999L;
        given(replyRepository.findById(replyId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> replyHideController.unhideReply(replyId));

        verify(replyRepository, times(1)).findById(replyId);
        verify(replyRepository, times(0)).save(any(Reply.class));
    }

    @Test
    @DisplayName("답변 숨김처리 시 visible 필드가 false로 변경된다")
    void hideReply_SetsVisibleToFalse() {
        // Arrange
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .replyId(replyId)
                .visible(true)
                .build();

        given(replyRepository.findById(replyId)).willReturn(Optional.of(reply));
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        replyHideController.hideReply(replyId);

        // Assert
        assertEquals(false, reply.getVisible());
        verify(replyRepository, times(1)).save(reply);
    }

    @Test
    @DisplayName("답변 숨김해제 시 visible 필드가 true로 변경된다")
    void unhideReply_SetsVisibleToTrue() {
        // Arrange
        Long replyId = 1L;
        Reply reply = Reply.builder()
                .replyId(replyId)
                .visible(false)
                .build();

        given(replyRepository.findById(replyId)).willReturn(Optional.of(reply));
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        // Act
        replyHideController.unhideReply(replyId);

        // Assert
        assertEquals(true, reply.getVisible());
        verify(replyRepository, times(1)).save(reply);
    }
}