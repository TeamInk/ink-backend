package net.ink.core.question.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.core.component.NumberRandomizer;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.ResourceNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.question.entity.Question;
import net.ink.core.question.repository.QuestionRepository;
import net.ink.core.reply.service.ReplyService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.ink.core.core.message.ErrorMessage.QUESTION_REFRESH_FAIL;

@Service
@RequiredArgsConstructor
public class TodayQuestionSelectionServiceImpl implements TodayQuestionSelectionService {
    private static final int MAX_TOLERATE = 1000;

    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ReplyService replyService;
    private final TodayQuestionService todayQuestionService;
    private final NumberRandomizer numberRandomizer;

    @Override
    @Transactional
    public Question reselectTodayQuestion(Member member) {
        Set<Long> checked = new HashSet<>();
        Question question = todayQuestionService.getTodayQuestionByMemberId(member.getMemberId());
        checked.add(question.getQuestionId());

        int highestId = getHighestId();
        int retry = 0;
        while (checked.size() < highestId) {
            retry++;
            if (retry > highestId * MAX_TOLERATE) {
                throw new BadRequestException(QUESTION_REFRESH_FAIL);
            }

            long randomId = numberRandomizer.getRandomInt(highestId);
            if (checked.contains(randomId)) {
                continue;
            }

            if (! questionService.existsById(randomId) ||
                    replyService.isQuestionAlreadyReplied(randomId, member.getMemberId())) {
                checked.add(randomId);
                continue;
            }

            try {
                question = questionService.getQuestionById(randomId);
                break;
            } catch (ResourceNotFoundException e) {
                // 질문이 존재 확인 후 삭제된 경우, 다음 질문 선택
                checked.add(randomId);
                continue;
            }
        }

        todayQuestionService.saveTodayQuestion(member.getMemberId(), question);
        return question;
    }

    private int getHighestId() {
        List<Question> questions = questionRepository.findAll(PageRequest.of(0, 1,
                Sort.by(Sort.Direction.DESC, "regDate")))
                .getContent();

        if (questions.isEmpty()) {
            throw new BadRequestException(QUESTION_REFRESH_FAIL);
        }

        return (int)(long)questions.get(0).getQuestionId();
    }
}
