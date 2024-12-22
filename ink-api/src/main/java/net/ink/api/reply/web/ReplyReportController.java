package net.ink.api.reply.web;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.ink.api.core.annotation.CurrentUser;
import net.ink.api.core.dto.ApiResult;
import net.ink.api.reply.component.ReplyReportMapper;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.core.member.entity.Member;
import net.ink.core.reply.entity.ReplyReport;
import net.ink.core.reply.service.ReplyReportService;

@Api(value = "답변 신고 엔드포인트", tags = "답변 신고 엔드포인트")
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
