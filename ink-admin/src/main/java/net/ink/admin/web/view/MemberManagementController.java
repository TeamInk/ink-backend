package net.ink.admin.web.view;

import lombok.RequiredArgsConstructor;
import net.ink.admin.dto.mapper.MemberMapper;
import net.ink.core.member.repository.MemberReportRepository;
import net.ink.core.member.repository.MemberRepository;
import net.ink.core.member.service.MemberService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class MemberManagementController {
    private final MemberReportRepository memberReportRepository;
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @GetMapping("/member-management")
    public String getMemberManagement(Model model) {
        model.addAttribute("inner", "member-management");
        model.addAttribute("members",
                memberService.findAllActiveMembers()
                        .stream().map(memberMapper::toDto).collect(Collectors.toList()));
        return "base";
    }

    @GetMapping("/member-report-management")
    public String getMemberReportManagement(Model model) {
        model.addAttribute("inner", "member-report-management");
        model.addAttribute("memberReports", memberReportRepository.findAll(Sort.by(Sort.Direction.DESC, "reportId")));
        return "base";
    }
}
