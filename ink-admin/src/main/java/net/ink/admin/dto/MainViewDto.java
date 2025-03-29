package net.ink.admin.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainViewDto {
    private int questionCount;
    private int registeredReplyCount;
    private int totalMemberCount;
    private List<UsefulExpressionDto> todayUsefulExpressions;
}
