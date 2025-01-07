package com.internet.workflow.infrastructure.persistence.repository;

import com.internet.workflow.infrastructure.persistence.entity.WorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<WorkflowEntity, String> {
    Optional<WorkflowEntity> findByOrderNumber(String orderNumber);
}
