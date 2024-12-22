package net.ink.admin.dto;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String category;

    @NotEmpty @Size(max = 73)
    @Pattern(regexp = "^[A-Za-z0-9\\s\\\\!@#$%^&*(),.?\":{}|<>]+$", message = "Word must only contain English letters, numbers, and special characters.")
    private String content;

    @Size(max = 45)
    private String koContent;
    private String authorName;
    private int repliesCount;
    private Set<WordHintDto> wordHints;
    private LocalDateTime regDate;
}
