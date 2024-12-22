package net.ink.api.reply.component;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.core.member.entity.Member;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.entity.ReplyReport;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Member.class, Reply.class})
public interface ReplyReportMapper {
    @Mapping(target = "reply", expression = "java(Reply.builder().replyId(replyReportDto.getReplyId()).build())")
    ReplyReport toEntity(ReplyReportDto replyReportDto);

    @Mapping(target = "replyId", expression = "java(replyReport.getReply().getReplyId())")
    @Mapping(target = "reporterId", expression = "java(replyReport.getReporter().getMemberId())")
    ReplyReportDto.ReadOnly toDto(ReplyReport replyReport);
}
