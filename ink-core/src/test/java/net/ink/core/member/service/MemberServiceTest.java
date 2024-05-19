package net.ink.core.member.service;

import static org.junit.jupiter.api.Assertions.*;

import net.ink.core.common.EntityCreator;
import net.ink.core.core.exception.BadRequestException;
import net.ink.core.core.exception.ResourceNotFoundException;
import net.ink.core.member.entity.Member;
import net.ink.core.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    public void setup() {
        Member member = EntityCreator.createMemberEntity();

        lenient().when(memberRepository.existsByNicknameAndIsActive(anyString(), anyBoolean())).thenReturn(false);
        lenient().when(memberRepository.existsByIdentifierAndIsActive(anyString(), anyBoolean())).thenReturn(true);
        lenient().when(memberRepository.existsByIdentifier(anyString())).thenReturn(true);
        lenient().when(memberRepository.findAllByIsActive(anyBoolean())).thenReturn(Collections.singletonList(member));
        lenient().when(memberRepository.findByIdentifierAndIsActive(anyString(), anyBoolean())).thenReturn(Optional.of(member));
        lenient().when(memberRepository.findByIdentifier(anyString())).thenReturn(Optional.of(member));
        lenient().when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
    }

    @Test
    @DisplayName("saveMember returns saved member when nickname is not duplicated")
    public void saveMemberReturnsSavedMemberWhenNicknameIsNotDuplicated() {
        Member newMember = new Member();
        newMember.setNickname("new");
        newMember.setIdentifier("new");
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(newMember);

        Member result = memberService.saveMember(newMember);

        assertNotNull(result);
        assertEquals(newMember.getNickname(), result.getNickname());
    }

    @Test
    @DisplayName("saveMember throws BadRequestException when nickname is duplicated")
    public void saveMemberThrowsBadRequestExceptionWhenNicknameIsDuplicated() {
        Member newMember = new Member();
        newMember.setNickname("test");
        newMember.setIdentifier("test");
        when(memberRepository.existsByNicknameAndIsActive(anyString(), anyBoolean())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> memberService.saveMember(newMember));
    }

    @Test
    @DisplayName("updateMember returns updated member")
    public void updateMemberReturnsUpdatedMember() {
        Member member = new Member();
        member.setNickname("updated");
        member.setIdentifier("updated");
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(member);

        Member result = memberService.updateMember(member);

        assertNotNull(result);
        assertEquals(member.getNickname(), result.getNickname());
    }

    @Test
    @DisplayName("dropOutMember sets isActive to false")
    public void dropOutMemberSetsIsActiveToFalse() {
        Member member = new Member();
        member.setNickname("drop");
        member.setIdentifier("drop");
        member.setIsActive(true);

        memberService.dropOutMember(member);

        assertFalse(member.getIsActive());
    }

    @Test
    @DisplayName("isNicknameDuplicated returns false when nickname is not duplicated")
    public void isNicknameDuplicatedReturnsFalseWhenNicknameIsNotDuplicated() {
        boolean result = memberService.isNicknameDuplicated("unique");

        assertFalse(result);
    }

    @Test
    @DisplayName("isMemberExist returns true when member exists")
    public void isMemberExistReturnsTrueWhenMemberExists() {
        boolean result = memberService.isMemberExist("test");

        assertTrue(result);
    }

    @Test
    @DisplayName("isMemberExistIncludingDropped returns true when member exists including dropped")
    public void isMemberExistIncludingDroppedReturnsTrueWhenMemberExistsIncludingDropped() {
        boolean result = memberService.isMemberExistIncludingDropped("test");

        assertTrue(result);
    }

    @Test
    @DisplayName("findAllActiveMembers returns list of active members")
    public void findAllActiveMembersReturnsListOfActiveMembers() {
        var result = memberService.findAllActiveMembers();

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("findByIdentifier returns member when member exists")
    public void findByIdentifierReturnsMemberWhenMemberExists() {
        Member result = memberService.findByIdentifier("test");

        assertNotNull(result);
    }

    @Test
    @DisplayName("findByIdentifier throws ResourceNotFoundException when member does not exist")
    public void findByIdentifierThrowsResourceNotFoundExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findByIdentifierAndIsActive(anyString(), anyBoolean())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.findByIdentifier("test"));
    }

    @Test
    @DisplayName("findByIdentifierIncludingDropped returns member when member exists including dropped")
    public void findByIdentifierIncludingDroppedReturnsMemberWhenMemberExistsIncludingDropped() {
        Member result = memberService.findByIdentifierIncludingDropped("test");

        assertNotNull(result);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when member does not exist")
    public void findByIdThrowsResourceNotFoundExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.findById(1L));
    }

    @Test
    @DisplayName("findById returns member when member exists")
    public void findByIdReturnsMemberWhenMemberExists() {
        Member result = memberService.findById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("findByIdentifierIncludingDropped throws ResourceNotFoundException when member does not exist")
    public void findByIdentifierIncludingDroppedThrowsResourceNotFoundExceptionWhenMemberDoesNotExist() {
        when(memberRepository.findByIdentifier(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.findByIdentifierIncludingDropped("test"));
    }
}