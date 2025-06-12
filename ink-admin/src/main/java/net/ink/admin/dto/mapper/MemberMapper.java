package net.ink.admin.dto.mapper;

import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import net.ink.admin.dto.MemberDto;
import net.ink.core.member.entity.Member;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = DateTimeFormatter.class)
public interface MemberMapper {
        @Mapping(target = "inkCount", expression = "java(member.getInkCookies().isEmpty() ? 0 : member.getInkCookies().size())")
        @Mapping(target = "attendanceCount", expression = "java(member.getMemberAttendance().getAttendanceCount())")
        @Mapping(target = "lastAttendanceDate", expression = "java(member.getMemberAttendance().getLastAttendanceDate()"
                        +
                        ".format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")) )")
        @Mapping(target = "isSuspended", source = "isSuspended")
        MemberDto toDto(Member member);
}
