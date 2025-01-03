package com.workflow.internet.equipment.infrastructure.persistence.repository;

import com.workflow.common.event.StepType;
import com.workflow.internet.equipment.infrastructure.persistence.entity.StepHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StepHistoryRepository extends JpaRepository<StepHistoryEntity, Long> {
    Optional<StepHistoryEntity> findByOrderNumber(String orderNumber);

    Optional<StepHistoryEntity> findByWorkflowIdAndStepType(String workflowId, StepType stepType);
}
