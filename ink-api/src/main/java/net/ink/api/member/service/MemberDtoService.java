package net.ink.api.member.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.member.component.MemberMapper;
import net.ink.api.member.dto.MemberDto;
import net.ink.core.cookie.repository.CookieAcquirementRepository;
import net.ink.core.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDtoService {
    private final MemberMapper memberMapper;
    private final CookieAcquirementRepository cookieAcquirementRepository;
    
    @Transactional(readOnly = true)
    public MemberDto.ReadOnly toDto(Member member) {
        MemberDto.ReadOnly dto = memberMapper.toDto(member);
        long inkCount = cookieAcquirementRepository.countByMemberMemberId(member.getMemberId());
        dto.setInkCount((int) inkCount);
        return dto;
    }
}