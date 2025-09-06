package net.ink.core.member.service;

import static net.ink.core.core.message.ErrorMessage.*;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.ResourceNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;

@Validated
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member saveMember(@Valid Member newMember) {
        if (isNicknameDuplicated(newMember.getNickname())) {
            throw new BadRequestException(DUPLICATED_NICKNAME);
        }

        if (isEmailDuplicated(newMember.getEmail())) {
            throw new BadRequestException(DUPLICATED_EMAIL);
        }

        if (!isIdentifierValid(newMember.getIdentifier())) {
            throw new BadRequestException(INVALID_IDENTIFIER);
        }

        return memberRepository.save(newMember);
    }

    @Transactional
    @CacheEvict(value = "members", key = "#member.memberId")
    public Member updateMember(@Valid Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public void dropOutMember(@Valid Member member) {
        member.setIsActive(false);
        memberRepository.save(member);
    }

    @Transactional
    public Member suspendMember(@Valid Member member, boolean isSuspended) {
        member.setIsSuspended(isSuspended);
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNicknameAndIsActive(nickname, true);
    }

    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmailAndIsActive(email, true);
    }

    private boolean isIdentifierValid(String identifier) {
        return identifier != null && identifier.matches("^kakao_\\d{10}$");
    }

    @Transactional(readOnly = true)
    public boolean isMemberExist(String identifier) {
        return memberRepository.existsByIdentifierAndIsActive(identifier, true);
    }

    @Transactional(readOnly = true)
    public boolean isMemberExistIncludingDropped(String identifier) {
        return memberRepository.existsByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public List<Member> findAllActiveMembers() {
        return memberRepository.findAllByIsActive(true);
    }

    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findByIdentifier(String identifier) {
        return memberRepository.findByIdentifierAndIsActive(identifier, true)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_EXIST_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member findByIdentifierIncludingDropped(String identifier) {
        return memberRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_EXIST_MEMBER));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "members", key = "#memberId")
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_EXIST_MEMBER));
    }
}
