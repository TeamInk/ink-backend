package net.ink.core.reply.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ink.core.reply.entity.ReplyReport;

@AllArgsConstructor
@Getter
public class ReplyReportEvent {
    private final ReplyReport replyReport;
}
