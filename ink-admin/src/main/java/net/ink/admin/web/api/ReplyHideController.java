package net.ink.admin.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;

@RestController
@RequiredArgsConstructor
public class ReplyHideController {
    private final ReplyRepository replyRepository;

    @AdminLogging
    @PutMapping("/api/reply/{replyId}/hide")
    public ResponseEntity<?> hideReply(@PathVariable Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply Id: " + replyId));

        reply.setVisible(false);
        replyRepository.save(reply);

        return ResponseEntity.ok().build();
    }

    @AdminLogging
    @PutMapping("/api/reply/{replyId}/unhide")
    public ResponseEntity<?> unhideReply(@PathVariable Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply Id: " + replyId));

        reply.setVisible(true);
        replyRepository.save(reply);

        return ResponseEntity.ok().build();
    }
}