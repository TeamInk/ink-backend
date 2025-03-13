package net.ink.core.member.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberAttendance {
    @Column(name = "attendance_count", nullable = false)
    private int attendanceCount;

    @Column(name = "last_attendance_date", nullable = false)
    @Builder.Default
    private LocalDate lastAttendanceDate = LocalDate.now();

    public void increaseAttendanceCount() {
        this.attendanceCount += 1;
        this.lastAttendanceDate = LocalDate.now();
    }
}
