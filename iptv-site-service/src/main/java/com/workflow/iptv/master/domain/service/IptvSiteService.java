package com.workflow.iptv.master.domain.service;

import com.workflow.common.event.WorkflowStepEvent;
import com.workflow.iptv.master.infrastructure.messaging.publisher.WorkflowStepResponsePublisher;
import com.workflow.iptv.master.infrastructure.persistence.entity.StepHistoryEntity;
import com.workflow.iptv.master.infrastructure.persistence.mapper.StepMapper;
import com.workflow.iptv.master.infrastructure.persistence.repository.StepHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IptvCertificationService {

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
            Thread.sleep(1000 * 5);

            StepHistoryEntity entity = stepHistoryRepository.findByWorkflowIdAndStepType(event.getWorkflowId(), event.getStepType())
                    .orElseThrow();

            entity.done();
            stepHistoryRepository.save(entity);
            publisher.publishIptvStepResponse(mapper.toEvent(entity));
        } catch (Exception ignored) {

        }
    }
}