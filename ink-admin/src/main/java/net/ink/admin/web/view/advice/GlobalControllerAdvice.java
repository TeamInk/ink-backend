package net.ink.admin.web.view.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import net.ink.admin.annotation.CurrentUser;
import net.ink.admin.entity.AdminMember;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserInfo(Model model, @CurrentUser AdminMember adminMember) {
        if (adminMember != null) {
            model.addAttribute("adminMember", adminMember);
        }
    }
}
