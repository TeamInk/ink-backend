package net.ink.core.reply.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ink.core.member.entity.Member;
import net.ink.core.question.entity.Question;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @Builder.Default
    @Column(nullable = false)
    private Boolean visible = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Builder.Default
    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate = LocalDateTime.now();

    @Builder.Default
    @Column(name = "mod_date", nullable = false)
    private LocalDateTime modDate = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private Set<ReplyLikes> replyLikes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private Set<ReplyReport> replyReports = new HashSet<>();

    public void modifyReply(Reply newReply) {
        this.content = newReply.getContent();
        this.image= newReply.getImage();
        this.modDate = LocalDateTime.now();
    }

    public boolean isAuthor(Member member){
        return this.author.getMemberId() == member.getMemberId();
    }

    public boolean likedByRequester(Member member){
        for(ReplyLikes replyLikes : this.replyLikes){
            if(replyLikes.getId().getMemberId() == member.getMemberId())
                return true;
        }
        return false;
    }

}
