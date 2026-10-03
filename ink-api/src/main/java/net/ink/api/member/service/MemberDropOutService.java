package net.ink.api.member.service;

import lombok.RequiredArgsConstructor;
import net.ink.api.core.oauth2.service.AppleRevokeService;
import net.ink.core.member.entity.Member;
import net.ink.core.member.service.MemberService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDropOutService {
    private final MemberService memberService;
    private final AppleRevokeService appleRevokeService;

    public void dropOut(Member member, String appleAuthorizationCode) {
        if (isAppleMember(member)) {
            appleRevokeService.revoke(member.getIdentifier(), appleAuthorizationCode);
        }

        memberService.dropOutMember(member);
    }

    private boolean isAppleMember(Member member) {
        return member.getIdentifier().startsWith(AppleRevokeService.IDENTIFIER_PREFIX);
    }
}
