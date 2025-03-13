package net.ink.admin.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.reply.repository.ReplyRepository;

@RestController
@RequiredArgsConstructor
public class ReplyDeleteController {
    private final ReplyRepository replyRepository;

    @AdminLogging
    @DeleteMapping("/api/reply/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        replyRepository.deleteById(replyId);
        return ResponseEntity.ok().build();
    }
}
