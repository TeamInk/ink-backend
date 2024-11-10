package net.ink.admin.web.view;

import lombok.RequiredArgsConstructor;
import net.ink.core.reply.repository.ReplyReportRepository;
import net.ink.core.reply.repository.ReplyRepository;
import net.ink.core.reply.service.ReplyService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReplyManagementViewController {
    private final ReplyRepository replyRepository;
    private final ReplyReportRepository replyReportRepository;

    @GetMapping("/reply-management")
    public String getReplyManagement(Model model) {
        model.addAttribute("inner", "reply-management");
        model.addAttribute("replies", replyRepository.findAll(Sort.by(Sort.Direction.DESC, "replyId")));
        return "base";
    }

    @GetMapping("/reply-report-management")
    public String getReplyReportManagement(Model model) {
        model.addAttribute("inner", "reply-report-management");
        model.addAttribute("replyReports", replyReportRepository.findAll(Sort.by(Sort.Direction.DESC, "reportId")));
        return "base";
    }
}
