package net.ink.admin.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsefulExpressionDto {
    private Long expId;

    @NotEmpty
    private String expression;

    @NotEmpty
    private String meaning;

    @NotEmpty
    private String expressionExample;

    @NotEmpty
    private String expressionExampleMeaning;
}
