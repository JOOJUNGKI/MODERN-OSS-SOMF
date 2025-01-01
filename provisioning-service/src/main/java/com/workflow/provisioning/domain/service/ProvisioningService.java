package com.workflow.provisioning.domain.service;

import com.workflow.provisioning.domain.event.WorkflowStepEvent;
import com.workflow.provisioning.infrastructure.messaging.publisher.WorkflowStepResponsePublisher;
import com.workflow.provisioning.infrastructure.persistence.entity.StepHistoryEntity;
import com.workflow.provisioning.infrastructure.persistence.mapper.StepMapper;
import com.workflow.provisioning.infrastructure.persistence.repository.StepHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProvisioningService {

    private final StepHistoryRepository stepHistoryRepository;

    private final WorkflowStepResponsePublisher publisher;

    private final StepMapper mapper;

    public void doProcess(WorkflowStepEvent event) {
        log.info("[Do Process] " + event.toString());
        saveHistroy(event);
        // 대기 넣어야 함
        done(event);
    }

    private void saveHistroy(WorkflowStepEvent event) {
        stepHistoryRepository.save(mapper.toEntity(event));
    }

    @Async
    private void done(WorkflowStepEvent event) {
        try {
            log.info("[DONE] " + event.toString());
            Thread.sleep(1000*5);

            StepHistoryEntity entity = stepHistoryRepository.findByWorkflowIdAndStepType(event.getWorkflowId(), event.getStepType())
                            .orElseThrow();

            entity.done();
            stepHistoryRepository.save(entity);
            publisher.publishInternetStepResponse(mapper.toEvent(entity));
        } catch (Exception ignored){

        }
    }

    public void handleStepCompletion(String workflowId, String result) {

//
//        WorkflowEntity entity = workflowRepository.findById(workflowId)
//                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
//
//        Workflow workflow = workflowMapper.toDomain(entity);
//        workflow.moveToNextStep();
//
//        workflowRepository.save(workflowMapper.toEntity(workflow));
//
//        if (workflow.getStatus() != WorkflowStatus.COMPLETED) {
//            stepRequestPublisher.publishStepRequest(workflow);
//        }
    }
}
