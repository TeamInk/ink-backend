package net.ink.api.reply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.ink.core.reply.entity.Reply;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="답변 신고 모델", description="답변에 대한 신고를 나타내는 모델")
public class ReplyReportDto {
    @NotNull
    @ApiModelProperty(value = "신고된 답변 ID", required = true, position = PropertyDisplayOrder.REPLY_ID)
    @JsonProperty(index = PropertyDisplayOrder.REPLY_ID)
    private Long replyId;

    @NotEmpty
    @ApiModelProperty(value = "신고 사유", required = true, position = PropertyDisplayOrder.REASON)
    @JsonProperty(index = PropertyDisplayOrder.REASON)
    private String reason;

    @ApiModelProperty(value = "신고자가 본인의 신고를 숨길지 여부", required = false, position = PropertyDisplayOrder.HIDE_TO_REPORTER)
    @JsonProperty(index = PropertyDisplayOrder.HIDE_TO_REPORTER)
    private Boolean hideToReporter = false;

    @SuperBuilder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value="답변 읽기 모델", description="질문에 대한 답변을 나타내는 읽기 전용 모델")
    public static class ReadOnly extends ReplyReportDto {
        @ApiModelProperty(value = "신고 ID", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = PropertyDisplayOrder.REPORT_ID)
        @JsonProperty(index = PropertyDisplayOrder.REPORT_ID)
        private Long reportId;

        @NotNull
        @ApiModelProperty(value = "신고자 ID", required = true, position = PropertyDisplayOrder.REPORTER_ID)
        @JsonProperty(index = PropertyDisplayOrder.REPORTER_ID)
        private Long reporterId;
    }

    private static class PropertyDisplayOrder {
        private static final int REPORT_ID           = 0;
        private static final int REPLY_ID            = 1;
        private static final int REPORTER_ID         = 2;
        private static final int REASON              = 3;
        private static final int HIDE_TO_REPORTER    = 4;
    }
}
