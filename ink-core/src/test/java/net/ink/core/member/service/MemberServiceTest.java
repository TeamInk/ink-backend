package net.ink.core.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import net.ink.core.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import net.ink.core.core.exception.BadRequestException;
import net.ink.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 테스트")
    void saveMember() {
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(new Member());

        Member newMember = new Member();
        newMember.setNickname("test");
        newMember.setEmail("test@example.com");

        Member savedMember = memberService.saveMember(newMember);
        assertNotNull(savedMember);
    }

    @Test
    @DisplayName("닉네임 중복 시 회원 저장 실패 테스트")
    void saveMemberButNicknameDuplicatedWillFail() {
        when(memberRepository.existsByNicknameAndIsActive(any(String.class), any(Boolean.class))).thenReturn(true);

        Member newMember = new Member();
        newMember.setNickname("test");
        newMember.setEmail("test@example.com");

        assertThrows(BadRequestException.class, () -> memberService.saveMember(newMember));
    }

    @Test
    @DisplayName("회원 업데이트 테스트")
    void updateMember() {
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(new Member());

        Member member = new Member();
        member.setNickname("updatedTest");
        member.setEmail("updatedTest@example.com");

        Member updatedMember = memberService.updateMember(member);
        assertNotNull(updatedMember);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void dropOutMember() {
        Member member = new Member();
        member.setIsActive(true);

        memberService.dropOutMember(member);

        assertFalse(member.getIsActive());
        verify(memberRepository, times(1)).saveAndFlush(member);
    }

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    void isNicknameDuplicated() {
        when(memberRepository.existsByNicknameAndIsActive(any(String.class), any(Boolean.class))).thenReturn(true);

        boolean isDuplicated = memberService.isNicknameDuplicated("test");
        assertTrue(isDuplicated);
    }

    @Test
    @DisplayName("회원 존재 확인 테스트")
    void isMemberExist() {
        when(memberRepository.existsByIdentifierAndIsActive(any(String.class), any(Boolean.class))).thenReturn(true);

        boolean isExist = memberService.isMemberExist("identifier");
        assertTrue(isExist);
    }

    @Test
    @DisplayName("회원 존재 확인 (탈퇴 포함) 테스트")
    void isMemberExistIncludingDropped() {
        when(memberRepository.existsByIdentifier(any(String.class))).thenReturn(true);

        boolean isExist = memberService.isMemberExistIncludingDropped("identifier");
        assertTrue(isExist);
    }

    @Test
    @DisplayName("활성 회원 모두 찾기 테스트")
    void findAllActiveMembers() {
        when(memberRepository.findAllByIsActive(any(Boolean.class))).thenReturn(List.of(new Member()));

        List<Member> activeMembers = memberService.findAllActiveMembers();
        assertFalse(activeMembers.isEmpty());
    }

    @Test
    @DisplayName("식별자로 회원 찾기 테스트")
    void findByIdentifier() {
        when(memberRepository.findByIdentifierAndIsActive(any(String.class), any(Boolean.class)))
                .thenReturn(Optional.of(new Member()));

        Member member = memberService.findByIdentifier("identifier");
        assertNotNull(member);
    }

    @Test
    @DisplayName("식별자로 회원 찾기 (탈퇴 포함) 테스트")
    void findByIdentifierIncludingDropped() {
        when(memberRepository.findByIdentifier(any(String.class)))
                .thenReturn(Optional.of(new Member()));

        Member member = memberService.findByIdentifierIncludingDropped("identifier");
        assertNotNull(member);
    }

    @Test
    @DisplayName("ID로 회원 찾기 테스트")
    void findById() {
        when(memberRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(new Member()));

        Member member = memberService.findById(1L);
        assertNotNull(member);
    }
}