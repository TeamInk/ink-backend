package net.ink.core.reply.repository;

import net.ink.core.reply.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByAuthorMemberIdAndVisibleOrderByReplyIdDesc(Long memberId, Boolean visible);
    List<Reply> findAllByQuestionQuestionIdAndVisible(Long questionId, Boolean visible);
    Page<Reply> findAllByQuestionQuestionIdAndVisible(Long questionId, Boolean visible, Pageable pageable);

    @Query("Select r from Reply r where r.visible = true and r.question.questionId = :questionId order by r.replyLikes.size desc")
    Page<Reply> findAllByQuestionQuestionIdOrderByReplyLikesSize(Long questionId, Pageable pageable);

    @Query("Select r from Reply r where r.visible = true order by r.replyLikes.size desc")
    Page<Reply> findAllByOrderByReplyLikesSize(Pageable pageable);

    boolean existsByRegDateBetweenAndAuthorMemberIdAndVisible(LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId, Boolean visible);
    boolean existsByQuestionQuestionIdAndAuthorMemberId(Long questionId, Long memberId);
    Optional<Reply> findByAuthorMemberIdAndQuestionQuestionIdAndVisible(Long memberId, Long questionId, Boolean visible);
    Page<Reply> findAllByVisible(Boolean visible, Pageable pageable);
}
