package net.ink.admin.service;

import lombok.RequiredArgsConstructor;
import net.ink.admin.dto.MemberDto;
import net.ink.admin.dto.mapper.MemberMapper;
import net.ink.core.cookie.repository.CookieAcquirementRepository;
import net.ink.core.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberDtoService {
    private final MemberMapper memberMapper;
    private final CookieAcquirementRepository cookieAcquirementRepository;
    
    @Transactional(readOnly = true)
    public MemberDto toDto(Member member) {
        MemberDto dto = memberMapper.toDto(member);
        long inkCount = cookieAcquirementRepository.countByMemberMemberId(member.getMemberId());
        dto.setInkCount((int) inkCount);
        return dto;
    }
}