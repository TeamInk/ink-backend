package net.ink.api.question.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.core.dto.ApiPageRequest;
import net.ink.core.question.entity.Question;
import net.ink.core.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionPaginationService {
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public Page<Question> getQuestionList(ApiPageRequest pageRequest, Long memberId) {
        Objects.requireNonNull(memberId, "memberId must not be null");
        if(pageRequest.getSort() == ApiPageRequest.PageSort.POPULAR)
            return questionRepository.findAllNotRepliedByMemberOrderByRepliesSizeDesc(memberId, pageRequest.convert());

        Sort newestSort = Sort.by(Sort.Direction.DESC, "regDate").and(Sort.by(Sort.Direction.DESC, "questionId"));
        return questionRepository.findAllNotRepliedByMember(memberId, PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), newestSort));
    }
}
