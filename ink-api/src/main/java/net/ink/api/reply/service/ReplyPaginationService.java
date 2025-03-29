package net.ink.api.reply.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.core.dto.ApiPageRequest;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.repository.ReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyPaginationService {
    private final ReplyRepository replyRepository;

    @Transactional(readOnly = true)
    public Page<Reply> findRepliesByQuestionId(Long questionId, ApiPageRequest pageRequest) {
        if (pageRequest.getSort() == ApiPageRequest.PageSort.POPULAR)
            return replyRepository.findAllByQuestionQuestionIdOrderByReplyLikesSize(questionId, pageRequest.convert());

        return replyRepository.findAllByQuestionQuestionIdAndVisibleAndDeleted(questionId, true, false, pageRequest.convertWithNewestSort());
    }

    @Transactional(readOnly = true)
    public Page<Reply> findReplies(ApiPageRequest pageRequest) {
        if (pageRequest.getSort() == ApiPageRequest.PageSort.POPULAR)
            return replyRepository.findAllByOrderByReplyLikesSize(pageRequest.convert());

        return replyRepository.findAllByVisibleAndDeleted(true, false, pageRequest.convertWithNewestSort());
    }
}
