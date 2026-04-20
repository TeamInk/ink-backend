package net.ink.core.question.repository;

import net.ink.core.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByAuthorMemberIdOrderByRegDateDesc(Long memberId);
    boolean existsByQuestionIdAndAuthorMemberId(Long questionId, Long memberId);

    @Query("Select q from Question q order by q.replies.size desc")
    Page<Question> findAllOrderByRepliesSizeDesc(Pageable page);

    @Query("Select q from Question q where not exists (select r from Reply r where r.question = q and r.author.memberId = :memberId) order by q.replies.size desc, q.questionId asc")
    Page<Question> findAllNotRepliedByMemberOrderByRepliesSizeDesc(@Param("memberId") Long memberId, Pageable page);

    @Query("Select q from Question q where not exists (select r from Reply r where r.question = q and r.author.memberId = :memberId)")
    Page<Question> findAllNotRepliedByMember(@Param("memberId") Long memberId, Pageable page);
}
