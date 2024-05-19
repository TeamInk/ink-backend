package net.ink.api.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="회원 신고 모델", description="회원에 대한 신고를 나타내는 모델")
public class MemberReportDto {
    @NotNull
    @ApiModelProperty(value = "신고된 회원 ID", required = true, position = PropertyDisplayOrder.TARGET_ID)
    @JsonProperty(index = PropertyDisplayOrder.TARGET_ID)
    private Long targetId;

    @NotEmpty
    @ApiModelProperty(value = "신고 사유", required = true, position = PropertyDisplayOrder.REASON)
    @JsonProperty(index = PropertyDisplayOrder.REASON)
    private String reason;

    @ApiModelProperty(value = "신고자에게 이 회원의 게시물을 숨김 여부 (기본: false)", required = false, position = PropertyDisplayOrder.HIDE_TO_REPORTER)
    @JsonProperty(index = PropertyDisplayOrder.HIDE_TO_REPORTER)
    private Boolean hideToReporter = false;

    @SuperBuilder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value="회원 신고 읽기 모델", description="회원에 대한 신고를 나타내는 읽기 전용 모델")
    public static class ReadOnly extends MemberReportDto {
        @ApiModelProperty(value = "신고 ID", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = PropertyDisplayOrder.REPORT_ID)
        @JsonProperty(index = PropertyDisplayOrder.REPORT_ID)
        private Long reportId;

        @NotNull
        @ApiModelProperty(value = "신고자 ID", position = PropertyDisplayOrder.REPORTER_ID)
        @JsonProperty(index = PropertyDisplayOrder.REPORTER_ID)
        private Long reporterId;
    }

    private static class PropertyDisplayOrder {
        private static final int REPORT_ID           = 0;
        private static final int TARGET_ID           = 1;
        private static final int REPORTER_ID         = 2;
        private static final int REASON              = 3;
        private static final int HIDE_TO_REPORTER    = 4;
    }
}
