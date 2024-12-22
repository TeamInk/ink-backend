package net.ink.api.reply.component;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.ink.api.common.DtoCreator;
import net.ink.api.reply.dto.ReplyReportDto;
import net.ink.core.common.EntityCreator;
import net.ink.core.reply.entity.ReplyReport;

@SpringBootTest
class ReplyReportMapperTest {
    @Autowired
    private ReplyReportMapper replyReportMapper;

    private final ReplyReport replyReport = EntityCreator.createReplyReportEntity();
    private final ReplyReportDto replyReportDto = DtoCreator.createReplyReportDto();

    @Test
    public void 엔티티에서_DTO변환_테스트() {
        ReplyReportDto.ReadOnly mappedDto = replyReportMapper.toDto(replyReport);

        assertNotNull(mappedDto);
        assertEquals(replyReport.getReportId(), mappedDto.getReportId());
        assertEquals(replyReport.getReply().getReplyId(), mappedDto.getReplyId());
        assertEquals(replyReport.getReporter().getMemberId(), mappedDto.getReporterId());
        assertEquals(replyReport.getReason(), mappedDto.getReason());
        assertEquals(replyReport.getHideToReporter(), mappedDto.getHideToReporter());
    }

    @Test
    public void DTO에서_엔티티변환_테스트() {
        ReplyReport mappedEntity = replyReportMapper.toEntity(replyReportDto);

        assertNotNull(mappedEntity);
        assertEquals(replyReportDto.getReplyId(), mappedEntity.getReply().getReplyId());
        assertEquals(replyReportDto.getReason(), mappedEntity.getReason());
        assertEquals(replyReportDto.getHideToReporter(), mappedEntity.getHideToReporter());
    }

}