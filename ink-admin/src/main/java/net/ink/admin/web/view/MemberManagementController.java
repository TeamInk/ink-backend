package net.ink.admin.web.view;

import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import net.ink.admin.dto.mapper.MemberMapper;
import net.ink.core.member.repository.MemberReportRepository;
import net.ink.core.member.service.MemberService;
import net.ink.core.reply.repository.ReplyRepository;

@RequiredArgsConstructor
@Controller
public class MemberManagementController {
    private final MemberReportRepository memberReportRepository;
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final ReplyRepository replyRepository;

    @GetMapping("/member-management")
    public String getMemberManagement(Model model) {
        model.addAttribute("inner", "member-management");
        model.addAttribute("members",
                memberService.findAllMembers()
                        .stream().map(memberMapper::toDto).collect(Collectors.toList()));
        return "base";
    }

    @GetMapping("/member-report-management")
    public String getMemberReportManagement(Model model) {
        model.addAttribute("inner", "member-report-management");
        model.addAttribute("memberReports",
                memberReportRepository.findAll(Sort.by(Sort.Direction.DESC, "reportId")).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                report -> report.getTarget().getMemberId(),
                                report -> report,
                                (existing, replacement) -> {
                                    existing.setReason(existing.getReason() + " + " + replacement.getReason());
                                    return existing;
                                }))
                        .values());
        return "base";
    }

    @GetMapping("/filtered-reply-management")
    public String getFilteredReplyManagement(@RequestParam("memberId") Long memberId, Model model) {
        model.addAttribute("inner", "reply-management");
        model.addAttribute("replies",
                replyRepository.findAllByAuthorMemberId(memberId, Sort.by(Sort.Direction.DESC, "replyId")));
        return "base";
    }
}
