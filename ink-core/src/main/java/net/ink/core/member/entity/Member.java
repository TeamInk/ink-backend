package net.ink.core.member.entity;

import lombok.*;
import net.ink.core.cookie.entity.CookieAcquirement;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(unique = true)
    private String email;

    @Column
    private String gender;

    @Column(name = "age_group")
    private String ageGroup;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(nullable = false, unique = true)
    private String nickname;

    private String image;

    @Builder.Default
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    @Builder.Default
    @Column(name = "mod_date", nullable = false)
    private LocalDateTime modDate = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CookieAcquirement> inkCookies = new HashSet<>();

    @Embedded
    @Builder.Default
    private MemberAttendance memberAttendance = new MemberAttendance();

    @Embedded
    @Builder.Default
    private MemberSetting memberSetting = new MemberSetting();

    public boolean isRequesterProfile(Long memberId){
        return this.memberId == memberId;
    }

    public void updateMember(Member member) {
        if (StringUtils.hasText(member.getNickname())) {
            this.nickname = member.getNickname();
            this.modDate = LocalDateTime.now();
        }

        if (StringUtils.hasText(member.getImage())) {
            this.image = member.getImage();
            this.modDate = LocalDateTime.now();
        }
    }

    public void increaseAttendanceCount() {
        this.memberAttendance = MemberAttendance.builder()
                .attendanceCount(this.memberAttendance.getAttendanceCount() + 1)
                .lastAttendanceDate(Instant.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
                .build();
    }
}
