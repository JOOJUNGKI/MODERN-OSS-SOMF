package com.workflow.api.controller;

import com.workflow.domain.service.WorkflowService;
import com.workflow.api.dto.WorkflowResponse;
import com.workflow.api.dto.WorkflowHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Tag(name = "워크플로우 API", description = "워크플로우 조회 API")
public class WorkflowController {
    private final WorkflowService workflowService;

    @GetMapping("/{workflowId}")
    @Operation(summary = "워크플로우 조회")
    public ResponseEntity<WorkflowResponse> getWorkflow(@PathVariable String workflowId) {
        return ResponseEntity.ok(workflowService.getWorkflow(workflowId));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "오더번호 워크플로우 조회")
    public ResponseEntity<WorkflowResponse> getWorkflowWithOrderNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(workflowService.getWorkflowWithOrderNumber(orderNumber));
    }


    @GetMapping("/{workflowId}/history")
    @Operation(summary = "워크플로우 이력 조회")
    public ResponseEntity<List<WorkflowHistoryResponse>> getWorkflowHistory(@PathVariable String workflowId) {
        return ResponseEntity.ok(workflowService.getWorkflowHistory(workflowId));
    }

}