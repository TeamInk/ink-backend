package net.ink.admin.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.core.member.entity.Member;
import net.ink.core.member.service.MemberService;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequiredArgsConstructor
public class MemberSuspendController {
    private final MemberService memberService;

    @Data
    public static class SuspendRequest {
        @JsonProperty("isSuspended")
        private boolean isSuspended;
    }

    @AdminLogging
    @PutMapping("/api/member/{memberId}/suspend")
    public ResponseEntity<?> suspendMember(@PathVariable Long memberId, @RequestBody SuspendRequest request) {
        Member member = memberService.findById(memberId);
        memberService.suspendMember(member, request.isSuspended);
        return ResponseEntity.ok().build();
    }
}