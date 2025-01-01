// File: myprj6/workflow-service/src/main/java/com/workflow/api/dto/WorkflowResponse.java
package com.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workflow.common.event.StepType;
import com.workflow.domain.model.workflow.WorkflowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@Schema(description = "워크플로우 응답")
public class WorkflowResponse {
    @Schema(description = "워크플로우 ID")
    private String id;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "상태")
    private WorkflowStatus status;

    @Schema(description = "활성 단계들")
    private Set<StepType> activeSteps;

    @Schema(description = "완료된 단계들")
    private Set<StepType> completedSteps;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
}