package net.ink.api.question.web;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.annotation.CurrentUser;
import net.ink.api.core.dto.ApiPageRequest;
import net.ink.api.core.dto.ApiPageResult;
import net.ink.api.core.dto.ApiResult;
import net.ink.api.reply.service.ReplyPaginationService;
import net.ink.core.member.entity.Member;
import net.ink.api.reply.component.ReplyMapper;
import net.ink.api.reply.dto.ReplyDto;
import net.ink.core.reply.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "질문에 대한 답변 엔드포인트", tags = "질문에 대한 답변 엔드포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionReplyController {
        private final ReplyService replyService;
        private final ReplyPaginationService replyPaginationService;
        private final ReplyMapper replyMapper;

        @ApiOperation(value = "질문 관련 답변 가져오기", notes = "질문 관련 답변 가져오기", response = ReplyDto.ReadOnly.class)
        @GetMapping("/{questionId}/replies")
        public ResponseEntity<ApiPageResult<ReplyDto.ReadOnly>> getQuestionReplies(
                        @ApiParam(value = "질문 id", required = true) @PathVariable Long questionId,
                        ApiPageRequest pageRequest,
                        @CurrentUser Member member) {

                return ResponseEntity.ok(
                                ApiPageResult.ok(
                                                replyPaginationService.findRepliesByQuestionId(questionId, pageRequest)
                                                                .map(x -> replyMapper.toDto(x, member))));
        }

        @ApiOperation(value = "질문에 대한 내 답변 가져오기", notes = "질문에 대한 내 답변 가져오기")
        @GetMapping("/{questionId}/replies/me")
        @ApiResponses({
                        @ApiResponse(code = 404, message = "답변이 존재하지 않습니다.")
        })
        public ResponseEntity<ApiResult<ReplyDto.ReadOnly>> getQuestionRepliesMe(
                        @ApiParam(value = "질문 id", required = true) @PathVariable Long questionId,
                        @CurrentUser Member member) {

                return ResponseEntity.ok(
                                ApiPageResult.ok(replyMapper.toDto(
                                                replyService.findReplyByQuestionIdAndMember(member.getMemberId(),
                                                                questionId),
                                                member)));
        }
}
