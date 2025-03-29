package net.ink.admin.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.ink.admin.dto.UsefulExpressionDto;
import net.ink.core.todayexpression.entity.UsefulExpression;

@Mapper(componentModel = "spring")
public interface UsefulExpressionMapper {
    UsefulExpressionDto toDto(UsefulExpression usefulExpression);

    @Mapping(target = "regDate", ignore = true)
    @Mapping(target = "modDate", ignore = true)
    @Mapping(target = "memberScraps", ignore = true)
    UsefulExpression toEntity(UsefulExpressionDto usefulExpression);
}
