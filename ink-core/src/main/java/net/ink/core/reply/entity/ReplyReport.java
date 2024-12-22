package net.ink.core.reply.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ink.core.member.entity.Member;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reply_report")
public class ReplyReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reply_id", nullable = false)
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Column(name = "reason")
    private String reason;

    @Builder.Default
    @Column(name = "hide_to_reporter", nullable = false)
    private Boolean hideToReporter = true;

    @Builder.Default
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProcessStatus status = ProcessStatus.PENDING;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    public enum ProcessStatus {
        PENDING,
        CANCELED,
        HIDED,
        DELETED
    }
}