package net.ink.admin.web.view;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;

@Controller
@RequiredArgsConstructor
public class ReplyManagementViewController {
    private final ReplyRepository replyRepository;
    private final ReplyReportRepository replyReportRepository;

    @GetMapping("/reply-management")
    public String getReplyManagement(Model model) {
        model.addAttribute("inner", "reply-management");
        model.addAttribute("replies", replyRepository.findAll(Sort.by(Sort.Direction.DESC, "replyId")).stream()
                .filter(reply -> !reply.getDeleted())
                .collect(java.util.stream.Collectors.toList()));
        return "base";
    }

    @GetMapping("/reply-report-management")
    public String getReplyReportManagement(Model model) {
        model.addAttribute("inner", "reply-report-management");
        model.addAttribute("replyReports",
                replyReportRepository.findAll(Sort.by(Sort.Direction.DESC, "reportId")).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                report -> report.getReply().getReplyId(),
                                report -> report,
                                (existing, replacement) -> {
                                    existing.setReason(existing.getReason() + " + " + replacement.getReason());
                                    return existing;
                                }))
                        .values());
        return "base";
    }

    @GetMapping("/reply-report-management-detail")
    public String getReplyReportManagementDetail(@RequestParam("reportId") Long reportId, Model model) {
        model.addAttribute("inner", "reply-report-management-detail");
        model.addAttribute("report", replyReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + reportId)));
        return "base";
    }
}
