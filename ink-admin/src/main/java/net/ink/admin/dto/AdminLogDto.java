package net.ink.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLogDto {
    private Long adminId;           // 로그 ID
    private String adminEmail;      // 액션을 수행한 관리자 이메일
    private String action;          // 수행된 액션 이름
    private String actionQuery;     // 액션의 세부 정보
    private LocalDateTime regDate;  // 로그 생성 시간
}
