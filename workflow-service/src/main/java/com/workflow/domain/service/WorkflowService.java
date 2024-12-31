// File: myprj6/workflow-service/src/main/java/com/workflow/domain/service/WorkflowService.java
package com.workflow.domain.service;

import com.workflow.api.dto.WorkflowHistoryResponse;
import com.workflow.api.dto.WorkflowResponse;
import com.workflow.common.exception.WorkflowNotFoundException;
import com.workflow.domain.event.WorkflowCreationEvent;
import com.workflow.domain.model.step.StepType;
import com.workflow.domain.model.workflow.Workflow;
import com.workflow.domain.model.workflow.WorkflowStatus;
import com.workflow.infrastructure.messaging.publisher.WorkflowStepRequestPublisher;
import com.workflow.infrastructure.persistence.entity.WorkflowEntity;
import com.workflow.infrastructure.persistence.mapper.WorkflowMapper;
import com.workflow.infrastructure.persistence.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final WorkflowStepRequestPublisher stepRequestPublisher;

    public Workflow createWorkflow(WorkflowCreationEvent event) {
        Workflow workflow = Workflow.create(
                event.getOrderNumber(),
                event.getOrderSeq(),
                event.getServiceType(),
                event.getOrderType(),
                event.getCustName(),
                event.getAddress());

        WorkflowEntity savedEntity = workflowRepository.save(workflowMapper.toEntity(workflow));

        // 초기 단계 이벤트 발행
        stepRequestPublisher.publishStepRequest(workflow);

        log.debug("Created new workflow: {}", workflow.getId());
        return workflowMapper.toDomain(savedEntity);
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflowWithOrderNumber(String orderNumber) {
        return workflowRepository.findByOrderNumber(orderNumber)
                .map(workflowMapper::toResponse)
                .orElseThrow(() -> new WorkflowNotFoundException("Order not found: " + orderNumber));
    }

    @Transactional(readOnly = true)
    public List<WorkflowHistoryResponse> getWorkflowHistory(String workflowId) {
        return workflowRepository.findById(workflowId)
                .map(entity -> workflowMapper.toHistoryResponses(entity.getStepHistories()))
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }


    public void handleStepCompletion(String workflowId, StepType completedStep) {
        WorkflowEntity entity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        Workflow workflow = workflowMapper.toDomain(entity);
        workflow.completeStep(completedStep);

        // 워크플로우 상태 업데이트
        WorkflowEntity updatedEntity = workflowRepository.save(workflowMapper.toEntity(workflow));
        Workflow updatedWorkflow = workflowMapper.toDomain(updatedEntity);

        // 다음 단계가 있는 경우 이벤트 발행
        if (updatedWorkflow.getStatus() != WorkflowStatus.COMPLETED
                && !updatedWorkflow.getActiveSteps().isEmpty()) {
            stepRequestPublisher.publishStepRequest(updatedWorkflow);
            log.debug("Published next step request for workflow: {}", workflowId);
        } else {
            log.info("Workflow completed: {}", workflowId);
        }
    }

    private void validateWorkflowExists(String workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new WorkflowNotFoundException(workflowId);
        }
    }
}