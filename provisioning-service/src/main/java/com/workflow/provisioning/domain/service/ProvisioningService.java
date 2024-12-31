package com.workflow.provisioning.domain.service;

import com.workflow.provisioning.domain.event.WorkflowStepEvent;
import com.workflow.provisioning.infrastructure.persistence.entity.StepHistoryEntity;
import com.workflow.provisioning.infrastructure.persistence.mapper.StepMapper;
import com.workflow.provisioning.infrastructure.persistence.repository.StepHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProvisioningService {

    private final StepHistoryRepository stepHistoryRepository;

    private final StepMapper mapper;

    public void doProcess(WorkflowStepEvent event) {
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
            Thread.sleep(1000*30);
            StepHistoryEntity entity = mapper.toEntity(event);
            entity.done();
            stepHistoryRepository.save(entity);
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
