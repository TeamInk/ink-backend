package net.ink.api.core.jwt.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import net.ink.core.core.exception.BadRequestException;

import java.util.stream.Stream;

@Getter
public enum TokenProvider {
    KAKAO,
    GOOGLE,
    APPLE;

    @JsonCreator
    public static TokenProvider create(String requestValue) {
        return Stream.of(values())
                .filter(v -> v.toString().equalsIgnoreCase(requestValue))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("잘못된 프로바이더입니다."));
    }
}
