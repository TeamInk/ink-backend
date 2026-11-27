package net.ink.core.reply.service;

import static net.ink.core.core.message.ErrorMessage.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ink.core.core.exception.AccessNotAllowedException;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.EntityNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.question.service.QuestionService;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final QuestionService questionService;
    private final ReplyPostProcessService replyPostProcessService;

    @Value("${media.base.dir.name}")
    private String mediaBaseDirName;
    private String replyImageDirName = "/reply";

    @Value("${spring.profiles.active}")
    private String profile;

    @PostConstruct
    protected void init() {
        replyImageDirName = mediaBaseDirName + replyImageDirName;
    }

    @Transactional
    public Reply create(Reply newReply) {
        Long memberId = newReply.getAuthor().getMemberId();
        Long questionId = newReply.getQuestion().getQuestionId();

        // 같은 내용의 등록 요청이 연달아 들어온 경우(더블탭, 재시도) 에러 대신 이미 저장된 답변을 그대로 반환한다.
        Optional<Reply> duplicatedReply = findDuplicatedReply(newReply);
        if (duplicatedReply.isPresent())
            return duplicatedReply.get();

        if (! isTest() && isAlreadyRepliedToday(memberId))
            throw new BadRequestException(ALREADY_ANSWERED_MEMBER);

        if (!questionService.existsById(questionId))
            throw new EntityNotFoundException(NOT_EXIST_QUESTION);

        if (isQuestionAlreadyReplied(questionId, memberId))
            throw new BadRequestException(ALREADY_ANSWERED_REPLY);

        newReply.setAuthor(newReply.getAuthor());
        newReply.setQuestion(questionService.getQuestionById(questionId));

        Reply savedReply = replyRepository.saveAndFlush(newReply);
        replyPostProcessService.postProcess(savedReply); // TODO 이벤트 기반으로 변경

        return savedReply;
    }

    private Optional<Reply> findDuplicatedReply(Reply newReply) {
        return replyRepository.findByAuthorMemberIdAndQuestionQuestionIdAndVisibleAndDeleted(
                        newReply.getAuthor().getMemberId(), newReply.getQuestion().getQuestionId(), true, false)
                .filter(saved -> Objects.equals(saved.getContent(), newReply.getContent())
                        && Objects.equals(saved.getImage(), newReply.getImage()));
    }

    private boolean isTest() {
        return "dev".equals(profile) || "dummy".equals(profile);
    }

    @Transactional(readOnly = true)
    public boolean isQuestionAlreadyReplied(Long questionId, Long authorId) {
        return replyRepository.existsByQuestionQuestionIdAndAuthorMemberId(questionId, authorId); // visible 여부는 여기서는 확인하지 않는다.
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyRepliedToday(Long memberId) {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)); // 오늘 00:00:00
        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)); //오늘 23:59:59

        return replyRepository.existsByRegDateBetweenAndAuthorMemberIdAndVisibleAndDeleted(startDateTime, endDatetime, memberId, true, false);
    }

    @Transactional
    public Reply update(Reply newReply) {
        Long replyId = newReply.getReplyId();

        Reply oldReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_REPLY));

        if (!oldReply.isAuthor(newReply.getAuthor()))
            throw new AccessNotAllowedException(UNAUTHORIZED);

        oldReply.modifyReply(newReply);

        return replyRepository.saveAndFlush(oldReply);
    }

    @Transactional
    public void delete(Member member, Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_REPLY));

        if (!reply.isAuthor(member))
            throw new AccessNotAllowedException(UNAUTHORIZED);

        replyRepository.deleteById(replyId);
    }

    @Transactional(readOnly = true)
    public Reply findReplyByQuestionIdAndMember(Long memberId, Long questionId) {
        return replyRepository.findByAuthorMemberIdAndQuestionQuestionIdAndVisibleAndDeleted(memberId, questionId, true, false)
                .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_REPLY));
    }

    @Transactional(readOnly = true)
    public List<Reply> findRepliesByMemberId(Long memberId){

        return replyRepository.findAllByAuthorMemberIdAndVisibleAndDeletedOrderByReplyIdDesc(memberId, true, false);
    }

    @Transactional(readOnly = true)
    public Reply findById(Long replyId){
        return replyRepository.findById(replyId).orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_REPLY));
    }
}
