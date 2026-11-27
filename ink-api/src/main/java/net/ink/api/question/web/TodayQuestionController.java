package net.ink.api.question.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.annotation.CurrentUser;
import net.ink.api.core.dto.ApiResult;
import net.ink.core.member.entity.Member;
import net.ink.api.question.component.QuestionMapper;
import net.ink.api.question.dto.QuestionDto;
import net.ink.api.question.dto.TodayQuestionDto;
import net.ink.core.question.entity.Question;
import net.ink.core.question.service.TodayQuestionSelectionService;
import net.ink.core.question.service.TodayQuestionService;
import net.ink.core.reply.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "오늘의 질문 엔드포인트", tags = "오늘의 질문 엔드포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/today-question")
public class TodayQuestionController {
    private final TodayQuestionService todayQuestionService;
    private final TodayQuestionSelectionService todayQuestionSelectionService;
    private final ReplyService replyService;
    private final QuestionMapper questionMapper;

    @ApiOperation(value = "오늘의 질문 가져오기", notes = "현재 로그인한 사용자의 오늘의 질문을 가져옵니다. 이미 답변한 질문이면 서버가 답변하지 않은 질문으로 재선택해서 반환합니다. "
            + "alreadyAnswered=true 는 더 이상 답변할 질문이 남아 있지 않은 경우에만 내려갑니다.")
    @GetMapping("")
    public ResponseEntity<ApiResult<TodayQuestionDto>> get(@CurrentUser Member member) {
        Question todayQuestion = todayQuestionService.getTodayQuestionByMemberId(member.getMemberId());
        if (replyService.isQuestionAlreadyReplied(todayQuestion.getQuestionId(), member.getMemberId())) {
            // 클라이언트의 /refresh 호출이나 스케줄러 배치에 의존하지 않도록 조회 시점에 재선택한다.
            todayQuestion = todayQuestionSelectionService.reselectTodayQuestion(member);
        }
        boolean alreadyAnswered = replyService.isQuestionAlreadyReplied(todayQuestion.getQuestionId(), member.getMemberId());
        return ResponseEntity.ok(ApiResult.ok(questionMapper.toTodayDto(todayQuestion, alreadyAnswered)));
    }

    @ApiOperation(value = "오늘의 질문 새로고침해서 가져오기", notes = "오늘의 질문을 새로고침 해서 가져옵니다.")
    @GetMapping("/refresh")
    public ResponseEntity<ApiResult<QuestionDto.ReadOnly>> getRefresh(@CurrentUser Member member) {
        return ResponseEntity.ok(ApiResult.ok(questionMapper.toDto(
                todayQuestionSelectionService.reselectTodayQuestion(member)
        )));
    }
}
