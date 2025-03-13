package net.ink.admin.dto.mapper;

import org.mapstruct.Mapper;

import net.ink.admin.dto.UsefulExpressionDto;
import net.ink.core.todayexpression.entity.UsefulExpression;

@Mapper(componentModel = "spring")
public interface UsefulExpressionMapper {
    UsefulExpressionDto toDto(UsefulExpression usefulExpression);
    UsefulExpression toEntity(UsefulExpressionDto usefulExpression);
}
