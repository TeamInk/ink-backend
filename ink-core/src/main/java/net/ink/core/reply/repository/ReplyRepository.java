package net.ink.core.reply.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.ink.core.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByAuthorMemberIdAndVisibleAndDeletedOrderByReplyIdDesc(Long memberId, Boolean visible,
            Boolean deleted);

    List<Reply> findAllByQuestionQuestionIdAndVisibleAndDeleted(Long questionId, Boolean visible, Boolean deleted);

    Page<Reply> findAllByQuestionQuestionIdAndVisibleAndDeleted(Long questionId, Boolean visible, Boolean deleted,
            Pageable pageable);

    @Query("Select r from Reply r where r.visible = true and r.deleted = false and r.question.questionId = :questionId order by r.replyLikes.size desc")
    Page<Reply> findAllByQuestionQuestionIdOrderByReplyLikesSize(Long questionId, Pageable pageable);

    @Query("Select r from Reply r where r.visible = true and r.deleted = false order by r.replyLikes.size desc")
    Page<Reply> findAllByOrderByReplyLikesSize(Pageable pageable);

    boolean existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(LocalDateTime startDateTime,
            LocalDateTime endDateTime, Long memberId, Boolean visible, Boolean deleted);

    boolean existsByQuestionQuestionIdAndAuthorMemberId(Long questionId, Long memberId);

    Optional<Reply> findByAuthorMemberIdAndQuestionQuestionIdAndVisibleAndDeleted(Long memberId, Long questionId,
            Boolean visible, Boolean deleted);

    Page<Reply> findAllByVisibleAndDeleted(Boolean visible, Boolean deleted, Pageable pageable);

    List<Reply> findAllByAuthorMemberId(Long memberId, Sort sort);
}
