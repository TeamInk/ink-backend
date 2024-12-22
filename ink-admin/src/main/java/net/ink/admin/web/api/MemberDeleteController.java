package net.ink.admin.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberDeleteController {
    private final MemberService memberService;

    @AdminLogging
    @DeleteMapping("/api/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId) {
        memberService.dropOutMember(memberService.findById(memberId)); // TODO : 하드 삭제로 변경
        return ResponseEntity.ok().build();
    }
}
