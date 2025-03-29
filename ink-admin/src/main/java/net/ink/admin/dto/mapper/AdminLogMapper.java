package net.ink.admin.dto.mapper;

import net.ink.admin.dto.AdminLogDto;
import net.ink.admin.entity.AdminLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AdminLogMapper {

    @Mapping(target = "adminEmail", source = "actionedAdminMember.email")
    AdminLogDto toDto(AdminLog adminLog);
}
