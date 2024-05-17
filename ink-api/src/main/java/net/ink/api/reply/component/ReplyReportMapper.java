package net.ink.api.reply.component;

import net.ink.api.member.component.MemberMapper;
import net.ink.api.question.component.QuestionMapper;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.reply.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

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
