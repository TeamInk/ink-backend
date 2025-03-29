package net.ink.admin.web.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import net.ink.core.todayexpression.repository.UsefulExpressionRepository;

@RequiredArgsConstructor
@Controller
public class UsefulExprManagementController {
    private final UsefulExpressionRepository usefulExpressionRepository;

    @GetMapping("/useful-expression-management")
    public String getMemberManagement(Model model) {
        model.addAttribute("inner", "useful-expression-management");
        model.addAttribute("usefulExpressions", usefulExpressionRepository.findAll());
        return "base";
    }
}
