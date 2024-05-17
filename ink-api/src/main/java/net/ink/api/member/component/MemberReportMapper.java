package net.ink.api.member.component;

import net.ink.api.member.dto.MemberReportDto;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.reply.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Member.class})
public interface MemberReportMapper {
    @Mapping(target = "target", expression = "java(Member.builder().memberId(memberReportDto.getTargetId()).build())")
    MemberReport toEntity(MemberReportDto memberReportDto);

    @Mapping(target = "reporterId", expression = "java(memberReport.getReporter().getMemberId())")
    @Mapping(target = "targetId", expression = "java(memberReport.getTarget().getMemberId())")
    MemberReportDto.ReadOnly toDto(MemberReport memberReport);
}
