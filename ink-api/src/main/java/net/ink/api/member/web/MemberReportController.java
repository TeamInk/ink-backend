package net.ink.api.member.web;


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
import net.ink.api.member.component.MemberReportMapper;
import net.ink.api.member.dto.MemberReportDto;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.service.MemberReportService;

@Api(value = "회원 신고 엔드포인트", tags = "회원 신고 엔드포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/report")
public class MemberReportController {
    private final MemberReportService memberReportService;
    private final MemberReportMapper memberReportMapper;

    @ApiOperation(value = "회원 신고하기", notes = "회원을 신고합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResult<MemberReportDto.ReadOnly>> postReportMember(@CurrentUser Member member, @RequestBody @Valid MemberReportDto memberReportDto) {
        MemberReport memberReport = memberReportMapper.toEntity(memberReportDto);
        memberReport.setReporter(member);
        return ResponseEntity.ok(ApiResult.ok((memberReportMapper.toDto(
                memberReportService.reportMember(memberReport))))
        );
    }
}
