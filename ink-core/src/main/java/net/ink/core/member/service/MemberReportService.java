package net.ink.core.member.service;

import lombok.RequiredArgsConstructor;
import net.ink.core.member.entity.MemberReport;
import net.ink.core.member.repository.MemberReportRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberReportService {
    private final MemberReportRepository memberReportRepository;

    @Transactional
    public void reportMember(MemberReport memberReport) {
        memberReportRepository.save(memberReport);
    }
}
