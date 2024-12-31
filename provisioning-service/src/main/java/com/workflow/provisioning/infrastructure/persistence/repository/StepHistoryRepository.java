package com.workflow.provisioning.infrastructure.persistence.repository;

import com.workflow.provisioning.infrastructure.persistence.entity.StepHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StepHistoryRepository extends JpaRepository<StepHistoryEntity, Long> {
    Optional<StepHistoryEntity> findByOrderNumber(String orderNumber);
}
