package net.ink.api.reply.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.annotation.CurrentUser;
import net.ink.api.core.dto.ApiPageRequest;
import net.ink.api.core.dto.ApiPageResult;
import net.ink.api.core.dto.ApiResult;
import net.ink.api.reply.component.ReplyMapper;
import net.ink.api.reply.component.ReplyReportMapper;
import net.ink.api.reply.dto.ReplyDto;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.api.reply.service.ReplyPaginationService;
import net.ink.core.badge.service.BadgeAccomplishedService;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.ReplyReport;
import net.ink.core.question.entity.Question;
import net.ink.core.reply.entity.Reply;
import net.ink.core.reply.service.ReplyReportService;
import net.ink.core.reply.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(value = "답변 엔드포인트", tags = "답변 엔드포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply/report")
public class ReplyReportController {
    private final ReplyReportService replyReportService;
    private final ReplyReportMapper replyReportMapper;

    @ApiOperation(value = "답변 신고하기", notes = "답변을 신고합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResult<ReplyReportDto.ReadOnly>> postReportReply(@CurrentUser Member member, @RequestBody @Valid ReplyReportDto replyReportDto) {
        ReplyReport replyReport = replyReportMapper.toEntity(replyReportDto);
        replyReport.setReporter(member);
        return ResponseEntity.ok(ApiResult.ok((replyReportMapper.toDto(
                replyReportService.reportReply(replyReport))))
        );
    }
}
