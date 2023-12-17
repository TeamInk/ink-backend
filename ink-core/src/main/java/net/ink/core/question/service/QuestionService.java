package net.ink.core.question.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.core.exception.ResourceNotFoundException;
import net.ink.core.question.entity.Question;
import net.ink.core.question.entity.WordHint;
import net.ink.core.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static net.ink.core.core.message.ErrorMessage.NOT_EXIST_QUESTION;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final WordHintService wordHintService;

    @Transactional(readOnly = true)
    public Question getQuestionById(Long questionId){
        return questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException(NOT_EXIST_QUESTION));
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long questionId){
        return questionRepository.existsById(questionId);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionWrittenByMember(Long memberId){
        return questionRepository.findAllByAuthorMemberIdOrderByRegDateDesc(memberId);
    }

    @Transactional
    public Question update(Question question){
        return this.create(question);
    }

    @Transactional
    public Question create(Question question){
        return questionRepository.saveAndFlush(question);
    }

    @Transactional
    public void deleteById(Long questionId) {
        Question question = this.getQuestionById(questionId);

        // 힌트를 먼저 삭제
        Set<WordHint> wordHints = question.getWordHints();
        for (WordHint wordHint : wordHints) {
            wordHintService.deleteById(wordHint.getHintId());
        }

        // 질문 삭제
        questionRepository.delete(question);
    }
}
