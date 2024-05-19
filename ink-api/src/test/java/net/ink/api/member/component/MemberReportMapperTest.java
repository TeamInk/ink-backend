package net.ink.api.member.component;

import net.ink.api.common.DtoCreator;
import net.ink.api.member.dto.MemberReportDto;
import net.ink.core.common.EntityCreator;
import net.ink.core.member.entity.MemberReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberReportMapperTest {
    @Autowired
    MemberReportMapper memberReportMapper;

    @Test
    void 엔티티에서_DTO변환_테스트() {
        // given
        MemberReport memberReport = EntityCreator.createMemberReportEntity();
        // when
        MemberReportDto.ReadOnly mappedDto = memberReportMapper.toDto(memberReport);

        // then
        assertNotNull(mappedDto);
        assertEquals(memberReport.getReportId(), mappedDto.getReportId());
        assertEquals(memberReport.getReporter().getMemberId(), mappedDto.getReporterId());
        assertEquals(memberReport.getTarget().getMemberId(), mappedDto.getTargetId());
        assertEquals(memberReport.getReason(), mappedDto.getReason());
        assertEquals(memberReport.getHideToReporter(), mappedDto.getHideToReporter());
    }

    @Test
    void DTO에서_엔티티변환_테스트() {
        // given
        MemberReportDto memberReportDto = DtoCreator.createMemberReportDto();
        // when
        MemberReport mappedEntity = memberReportMapper.toEntity(memberReportDto);

        // then
        assertNotNull(mappedEntity);
        assertEquals(memberReportDto.getTargetId(), mappedEntity.getTarget().getMemberId());
        assertEquals(memberReportDto.getReason(), mappedEntity.getReason());
        assertEquals(memberReportDto.getHideToReporter(), mappedEntity.getHideToReporter());
    }
}