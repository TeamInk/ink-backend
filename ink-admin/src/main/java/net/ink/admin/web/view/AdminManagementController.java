package net.ink.admin.web.view;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import net.ink.admin.repository.AdminMemberRepository;

@RequiredArgsConstructor
@Controller
public class AdminManagementController {
    private final AdminMemberRepository adminMemberRepository;

    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    @GetMapping("/admin-management")
    public String getMemberManagement(Model model) {
        model.addAttribute("inner", "admin-management");
        model.addAttribute("adminMembers", adminMemberRepository.findAll());
        return "base";
    }
}
