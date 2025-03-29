package net.ink.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long memberId;
    private int inkCount;
    private int attendanceCount;
    private String lastAttendanceDate;
    private String identifier;
    private String email;
    private String nickname;
    private String image;
    private Boolean isActive;
}
