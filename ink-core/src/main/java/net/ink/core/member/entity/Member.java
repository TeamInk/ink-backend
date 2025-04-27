package net.ink.core.member.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ink.core.cookie.entity.CookieAcquirement;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder.Default
    @Column(name = "is_suspended", nullable = false)
    private Boolean isSuspended = false;

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

    @Builder.Default
    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    private Set<MemberReport> memberReports = new HashSet<>();

    public boolean isRequesterProfile(Long memberId) {
        return this.memberId.equals(memberId);
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
        this.memberAttendance.increaseAttendanceCount();
    }
}
