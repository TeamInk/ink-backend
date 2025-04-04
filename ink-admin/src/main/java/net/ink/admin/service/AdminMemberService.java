package net.ink.admin.service;

import static net.ink.core.core.message.ErrorMessage.*;

import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ink.admin.dto.AdminUser;
import net.ink.admin.entity.AdminMember;
import net.ink.admin.repository.AdminMemberRepository;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final AdminMemberRepository adminMemberRepository;
    private final AdminEmailService adminEmailService;

    @Transactional
    public AdminMember saveAdminMember(@Valid AdminMember newMember) {
        if (isNicknameDuplicated(newMember.getNickname())) {
            throw new BadRequestException(DUPLICATED_NICKNAME);
        }

        if (isEmailDuplicated(newMember.getEmail())) {
            throw new BadRequestException(DUPLICATED_NICKNAME);
        }

        return adminMemberRepository.saveAndFlush(newMember);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String email) {
        return adminMemberRepository.existsByNicknameAndIsActive(email, true);
    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return adminMemberRepository.existsByEmailAndIsActive(email, true);
    }

    @Transactional
    public void deleteAdminMemberById(Long adminId) {
        AdminMember adminMember = adminMemberRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_MEMBER));

        if (adminMember.getAdminRank() == AdminMember.RANK.SUPERVISOR) {
            throw new BadRequestException("슈퍼바이저는 삭제할 수 없습니다.");
        }

        adminMember.setIsActive(false);
    }

    @Transactional
    public void promoteAdminMemberById(Long adminId) {
        AdminMember adminMember = adminMemberRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_MEMBER));

        boolean sendEmail = false;
        String emailSubject = "";
        String emailContent = "";

        if (adminMember.getAdminRank() == AdminMember.RANK.PENDING) {
            adminMember.setAdminRank(AdminMember.RANK.MANAGER);
            sendEmail = true;
            emailSubject = "승급 안내";
            emailContent = "축하합니다! 매니저로 승급되셨습니다.";
        } else if (adminMember.getAdminRank() == AdminMember.RANK.MANAGER) {
            adminMember.setAdminRank(AdminMember.RANK.SUPERVISOR);
            sendEmail = true;
            emailSubject = "승급 안내";
            emailContent = "축하합니다! 슈퍼바이저로 승급되셨습니다.";

            AdminMember currentMember = ((AdminUser) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getAdminMember();
            currentMember = adminMemberRepository.findById(currentMember.getAdminId())
                    .orElseThrow(() -> new EntityNotFoundException(NOT_EXIST_MEMBER));
            currentMember.setAdminRank(AdminMember.RANK.MANAGER);

            SecurityContextHolder.clearContext();
        }

        if (sendEmail) {
            String toEmail = adminMember.getEmail();
            adminEmailService.sendPromotionEmail(toEmail, emailSubject, emailContent);
        }
    }
}
