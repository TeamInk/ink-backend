package net.ink.core.member.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import net.ink.core.annotation.InkDataTest;
import net.ink.core.member.entity.Member;
import net.ink.core.member.entity.MemberAttendance;
import net.ink.core.member.entity.MemberSetting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@InkDataTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @ExpectedDatabase(value = "classpath:dbunit/expected/멤버_회원가입.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 멤버_회원가입() {
        Member member = Member.builder()
                .memberId(1L)
                .identifier("123")
                .email("test@gmail.com")
                .isActive(true)
                .nickname("Test")
                .regDate(LocalDateTime.of(2020, 10, 14, 17, 11, 9))
                .modDate(LocalDateTime.of(2020, 10, 14, 17, 11, 10))
                .build();

        memberRepository.save(member);
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    public void 멤버_연관_설정_조회() {
        Member member = memberRepository.findById(2L).orElseThrow();
        MemberSetting memberSetting = member.getMemberSetting();

        assertTrue(memberSetting.isDailyPushActive());
        assertFalse(memberSetting.isLikePushActive());
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    public void 멤버_연관_출석_조회() {
        Member member = memberRepository.findById(1L).orElseThrow();
        MemberAttendance memberAttendance = member.getMemberAttendance();

        assertEquals(10, memberAttendance.getAttendanceCount());
        assertEquals(2020, memberAttendance.getLastAttendanceDate().getYear());
        assertEquals(10, memberAttendance.getLastAttendanceDate().getMonthValue());
        assertEquals(14, memberAttendance.getLastAttendanceDate().getDayOfMonth());
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    public void 닉네임_존재_조회() {
        assertTrue(memberRepository.existsByNicknameAndIsActive("Test", true));
        assertFalse(memberRepository.existsByNicknameAndIsActive("Non-exist", true));
        assertFalse(memberRepository.existsByNicknameAndIsActive("Test3", true));
    }

    // @Test
    // @DatabaseSetup({
    // "classpath:dbunit/entity/member.xml"
    // })
    // public void 이메일_존재_조회(){
    // assertTrue(memberRepository.existsByEmail("test@gmail.com"));
    // assertFalse(memberRepository.existsByEmail("bleum@gmail.com"));
    // }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    public void 식별자_존재_조회() {
        assertTrue(memberRepository.existsByIdentifierAndIsActive("123", true));
        assertFalse(memberRepository.existsByIdentifierAndIsActive("125", true));
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    public void 회원_전체_조회() {
        assertEquals(2, memberRepository.findAllByIsActive(true).size());
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    @ExpectedDatabase(value = "classpath:dbunit/expected/멤버_정지상태_변경.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 멤버_정지_처리() {
        Member member = memberRepository.findById(1L).orElseThrow();
        assertFalse(member.getIsSuspended()); // 변경 전 상태 확인

        member.setIsSuspended(true);
        memberRepository.saveAndFlush(member);

        entityManager.clear();

        Member updatedMember = memberRepository.findById(1L).orElseThrow();
        assertTrue(updatedMember.getIsSuspended()); // 변경 후 상태 확인
    }

    @Test
    @DatabaseSetup({
            "classpath:dbunit/entity/member.xml"
    })
    @ExpectedDatabase(value = "classpath:dbunit/expected/멤버_정지상태_해제.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void 멤버_정지_해제() {
        // 멤버3는 정지 상태(is_suspended=true)임
        Member member = memberRepository.findById(3L).orElseThrow();
        assertTrue(member.getIsSuspended()); // 변경 전 상태 확인

        member.setIsSuspended(false);
        memberRepository.saveAndFlush(member);

        entityManager.clear();

        Member updatedMember = memberRepository.findById(3L).orElseThrow();
        assertFalse(updatedMember.getIsSuspended()); // 변경 후 상태 확인
    }
}
