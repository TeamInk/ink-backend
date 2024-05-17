package net.ink.core.reply.service.event;

import lombok.RequiredArgsConstructor;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReplyReportEventListener {
    private final ReplyRepository replyRepository;
    private final ReplyReportRepository replyReportRepository;

    @Async
    @EventListener
    public void handleReplyReportEvent(ReplyReportEvent event) {
        int count = replyReportRepository.countByReply(event.getReplyReport().getReply());

        if (count >= 3) {
            replyRepository.delete(event.getReplyReport().getReply());
        }
    }
}
