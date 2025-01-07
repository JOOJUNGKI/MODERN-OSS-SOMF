
package com.iptv.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workflow.common.step.StepTypeStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "워크플로우 이력 응답")
public class WorkflowHistoryResponse {
    @Schema(description = "단계 유형")
    private StepTypeStrategy stepType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "시작 시간")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "종료 시간")
    private LocalDateTime endTime;
}
