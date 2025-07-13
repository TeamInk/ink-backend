package net.ink.core.member.service;

import static net.ink.core.core.message.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.ResourceNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("멤버 저장 성공 테스트")
    void saveMember_Success() {
        // Arrange
        Member newMember = Member.builder()
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("testUser")
                .isActive(true)
                .isSuspended(false)
                .build();

        given(memberRepository.existsByNicknameAndIsActive("testUser", true)).willReturn(false);
        given(memberRepository.existsByEmailAndIsActive("test@example.com", true)).willReturn(false);
        given(memberRepository.saveAndFlush(newMember)).willReturn(newMember);

        // Act
        Member savedMember = memberService.saveMember(newMember);

        // Assert
        assertNotNull(savedMember);
        assertEquals("testUser", savedMember.getNickname());
        assertEquals("test@example.com", savedMember.getEmail());
        verify(memberRepository).saveAndFlush(newMember);
    }

    @Test
    @DisplayName("멤버 저장 실패 - 닉네임 중복")
    void saveMember_DuplicatedNickname() {
        // Arrange
        Member newMember = Member.builder()
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("duplicatedNickname")
                .build();

        given(memberRepository.existsByNicknameAndIsActive("duplicatedNickname", true)).willReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            memberService.saveMember(newMember);
        });

        assertEquals(DUPLICATED_NICKNAME, exception.getMessage());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("멤버 저장 실패 - 이메일 중복")
    void saveMember_DuplicatedEmail() {
        // Arrange
        Member newMember = Member.builder()
                .identifier("kakao_1234567890")
                .email("duplicated@example.com")
                .nickname("testUser")
                .build();

        given(memberRepository.existsByNicknameAndIsActive("testUser", true)).willReturn(false);
        given(memberRepository.existsByEmailAndIsActive("duplicated@example.com", true)).willReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            memberService.saveMember(newMember);
        });

        assertEquals(DUPLICATED_EMAIL, exception.getMessage());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("멤버 저장 실패 - 잘못된 식별자")
    void saveMember_InvalidIdentifier() {
        // Arrange
        Member newMember = Member.builder()
                .identifier("invalid_identifier")
                .email("test@example.com")
                .nickname("testUser")
                .build();

        given(memberRepository.existsByNicknameAndIsActive("testUser", true)).willReturn(false);
        given(memberRepository.existsByEmailAndIsActive("test@example.com", true)).willReturn(false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            memberService.saveMember(newMember);
        });

        assertEquals(INVALID_IDENTIFIER, exception.getMessage());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("멤버 업데이트 성공 테스트")
    void updateMember_Success() {
        // Arrange
        Member member = Member.builder()
                .memberId(1L)
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("updatedUser")
                .build();

        given(memberRepository.saveAndFlush(member)).willReturn(member);

        // Act
        Member updatedMember = memberService.updateMember(member);

        // Assert
        assertNotNull(updatedMember);
        assertEquals("updatedUser", updatedMember.getNickname());
        verify(memberRepository).saveAndFlush(member);
    }

    @Test
    @DisplayName("멤버 탈퇴 처리 테스트")
    void dropOutMember_Success() {
        // Arrange
        Member member = Member.builder()
                .memberId(1L)
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("testUser")
                .isActive(true)
                .build();

        given(memberRepository.saveAndFlush(member)).willReturn(member);

        // Act
        memberService.dropOutMember(member);

        // Assert
        assertFalse(member.getIsActive());
        verify(memberRepository).saveAndFlush(member);
    }

    @Test
    @DisplayName("멤버 정지 처리 테스트")
    void suspendMember_Success() {
        // Arrange
        Member member = Member.builder()
                .memberId(1L)
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("testUser")
                .isSuspended(false)
                .build();

        given(memberRepository.saveAndFlush(member)).willReturn(member);

        // Act
        Member suspendedMember = memberService.suspendMember(member, true);

        // Assert
        assertTrue(suspendedMember.getIsSuspended());
        verify(memberRepository).saveAndFlush(member);
    }

    @Test
    @DisplayName("멤버 정지 해제 테스트")
    void unsuspendMember_Success() {
        // Arrange
        Member member = Member.builder()
                .memberId(1L)
                .identifier("kakao_1234567890")
                .email("test@example.com")
                .nickname("testUser")
                .isSuspended(true)
                .build();

        given(memberRepository.saveAndFlush(member)).willReturn(member);

        // Act
        Member unsuspendedMember = memberService.suspendMember(member, false);

        // Assert
        assertFalse(unsuspendedMember.getIsSuspended());
        verify(memberRepository).saveAndFlush(member);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 중복된 경우")
    void isNicknameDuplicated_True() {
        // Arrange
        String nickname = "duplicatedNickname";
        given(memberRepository.existsByNicknameAndIsActive(nickname, true)).willReturn(true);

        // Act
        boolean result = memberService.isNicknameDuplicated(nickname);

        // Assert
        assertTrue(result);
        verify(memberRepository).existsByNicknameAndIsActive(nickname, true);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 중복되지 않은 경우")
    void isNicknameDuplicated_False() {
        // Arrange
        String nickname = "uniqueNickname";
        given(memberRepository.existsByNicknameAndIsActive(nickname, true)).willReturn(false);

        // Act
        boolean result = memberService.isNicknameDuplicated(nickname);

        // Assert
        assertFalse(result);
        verify(memberRepository).existsByNicknameAndIsActive(nickname, true);
    }

    @Test
    @DisplayName("이메일 중복 체크 - 중복된 경우")
    void isEmailDuplicated_True() {
        // Arrange
        String email = "duplicated@example.com";
        given(memberRepository.existsByEmailAndIsActive(email, true)).willReturn(true);

        // Act
        boolean result = memberService.isEmailDuplicated(email);

        // Assert
        assertTrue(result);
        verify(memberRepository).existsByEmailAndIsActive(email, true);
    }

    @Test
    @DisplayName("이메일 중복 체크 - 중복되지 않은 경우")
    void isEmailDuplicated_False() {
        // Arrange
        String email = "unique@example.com";
        given(memberRepository.existsByEmailAndIsActive(email, true)).willReturn(false);

        // Act
        boolean result = memberService.isEmailDuplicated(email);

        // Assert
        assertFalse(result);
        verify(memberRepository).existsByEmailAndIsActive(email, true);
    }

    @Test
    @DisplayName("멤버 존재 여부 확인 - 존재하는 경우")
    void isMemberExist_True() {
        // Arrange
        String identifier = "kakao_1234567890";
        given(memberRepository.existsByIdentifierAndIsActive(identifier, true)).willReturn(true);

        // Act
        boolean result = memberService.isMemberExist(identifier);

        // Assert
        assertTrue(result);
        verify(memberRepository).existsByIdentifierAndIsActive(identifier, true);
    }

    @Test
    @DisplayName("멤버 존재 여부 확인 - 존재하지 않는 경우")
    void isMemberExist_False() {
        // Arrange
        String identifier = "kakao_9999999999";
        given(memberRepository.existsByIdentifierAndIsActive(identifier, true)).willReturn(false);

        // Act
        boolean result = memberService.isMemberExist(identifier);

        // Assert
        assertFalse(result);
        verify(memberRepository).existsByIdentifierAndIsActive(identifier, true);
    }

    @Test
    @DisplayName("탈퇴 포함 멤버 존재 여부 확인 - 존재하는 경우")
    void isMemberExistIncludingDropped_True() {
        // Arrange
        String identifier = "kakao_1234567890";
        given(memberRepository.existsByIdentifier(identifier)).willReturn(true);

        // Act
        boolean result = memberService.isMemberExistIncludingDropped(identifier);

        // Assert
        assertTrue(result);
        verify(memberRepository).existsByIdentifier(identifier);
    }

    @Test
    @DisplayName("탈퇴 포함 멤버 존재 여부 확인 - 존재하지 않는 경우")
    void isMemberExistIncludingDropped_False() {
        // Arrange
        String identifier = "kakao_9999999999";
        given(memberRepository.existsByIdentifier(identifier)).willReturn(false);

        // Act
        boolean result = memberService.isMemberExistIncludingDropped(identifier);

        // Assert
        assertFalse(result);
        verify(memberRepository).existsByIdentifier(identifier);
    }

    @Test
    @DisplayName("활성 멤버 전체 조회 테스트")
    void findAllActiveMembers_Success() {
        // Arrange
        List<Member> activeMembers = Arrays.asList(
                Member.builder().memberId(1L).nickname("user1").isActive(true).build(),
                Member.builder().memberId(2L).nickname("user2").isActive(true).build());
        given(memberRepository.findAllByIsActive(true)).willReturn(activeMembers);

        // Act
        List<Member> result = memberService.findAllActiveMembers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Member::getIsActive));
        verify(memberRepository).findAllByIsActive(true);
    }

    @Test
    @DisplayName("전체 멤버 조회 테스트")
    void findAllMembers_Success() {
        // Arrange
        List<Member> allMembers = Arrays.asList(
                Member.builder().memberId(1L).nickname("user1").isActive(true).build(),
                Member.builder().memberId(2L).nickname("user2").isActive(false).build());
        given(memberRepository.findAll()).willReturn(allMembers);

        // Act
        List<Member> result = memberService.findAllMembers();

        // Assert
        assertEquals(2, result.size());
        verify(memberRepository).findAll();
    }

    @Test
    @DisplayName("식별자로 멤버 조회 성공 테스트")
    void findByIdentifier_Success() {
        // Arrange
        String identifier = "kakao_1234567890";
        Member member = Member.builder()
                .memberId(1L)
                .identifier(identifier)
                .nickname("testUser")
                .isActive(true)
                .build();
        given(memberRepository.findByIdentifierAndIsActive(identifier, true)).willReturn(Optional.of(member));

        // Act
        Member result = memberService.findByIdentifier(identifier);

        // Assert
        assertNotNull(result);
        assertEquals(identifier, result.getIdentifier());
        verify(memberRepository).findByIdentifierAndIsActive(identifier, true);
    }

    @Test
    @DisplayName("식별자로 멤버 조회 실패 - 존재하지 않는 멤버")
    void findByIdentifier_NotFound() {
        // Arrange
        String identifier = "kakao_9999999999";
        given(memberRepository.findByIdentifierAndIsActive(identifier, true)).willReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.findByIdentifier(identifier);
        });

        assertEquals(NOT_EXIST_MEMBER, exception.getMessage());
        verify(memberRepository).findByIdentifierAndIsActive(identifier, true);
    }

    @Test
    @DisplayName("탈퇴 포함 식별자로 멤버 조회 성공 테스트")
    void findByIdentifierIncludingDropped_Success() {
        // Arrange
        String identifier = "kakao_1234567890";
        Member member = Member.builder()
                .memberId(1L)
                .identifier(identifier)
                .nickname("testUser")
                .isActive(false)
                .build();
        given(memberRepository.findByIdentifier(identifier)).willReturn(Optional.of(member));

        // Act
        Member result = memberService.findByIdentifierIncludingDropped(identifier);

        // Assert
        assertNotNull(result);
        assertEquals(identifier, result.getIdentifier());
        verify(memberRepository).findByIdentifier(identifier);
    }

    @Test
    @DisplayName("탈퇴 포함 식별자로 멤버 조회 실패 - 존재하지 않는 멤버")
    void findByIdentifierIncludingDropped_NotFound() {
        // Arrange
        String identifier = "kakao_9999999999";
        given(memberRepository.findByIdentifier(identifier)).willReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.findByIdentifierIncludingDropped(identifier);
        });

        assertEquals(NOT_EXIST_MEMBER, exception.getMessage());
        verify(memberRepository).findByIdentifier(identifier);
    }

    @Test
    @DisplayName("ID로 멤버 조회 성공 테스트")
    void findById_Success() {
        // Arrange
        Long memberId = 1L;
        Member member = Member.builder()
                .memberId(memberId)
                .identifier("kakao_1234567890")
                .nickname("testUser")
                .build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // Act
        Member result = memberService.findById(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("ID로 멤버 조회 실패 - 존재하지 않는 멤버")
    void findById_NotFound() {
        // Arrange
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.findById(memberId);
        });

        assertEquals(NOT_EXIST_MEMBER, exception.getMessage());
        verify(memberRepository).findById(memberId);
    }
}