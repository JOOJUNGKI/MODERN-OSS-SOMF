package com.iptv.workflow.infrastructure.persistence.entity;

import com.workflow.common.step.StepTypeStrategy;
import com.iptv.workflow.domain.model.workflow.WorkflowStatus;
import com.iptv.workflow.infrastructure.persistence.converter.StepTypeStrategyConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "workflows")
@Getter
@Setter
public class WorkflowEntity {
   @Id
   private String id;

   @Column(unique = true)
   private String orderNumber;
   private Integer orderSeq;
   private String serviceType;
   private String orderType;
   private String custName;
   private String address;

   @Enumerated(EnumType.STRING)
   private WorkflowStatus status;

   @ElementCollection
   @Convert(converter = StepTypeStrategyConverter.class)
   @CollectionTable(
           name = "workflow_active_steps",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   @Column(name = "step_type")
   private Set<StepTypeStrategy> activeSteps = new HashSet<>();

   @ElementCollection
   @Convert(converter = StepTypeStrategyConverter.class)
   @CollectionTable(
           name = "workflow_completed_steps",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   @Column(name = "step_type")
   private Set<StepTypeStrategy> completedSteps = new HashSet<>();

   @CreationTimestamp
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<StepHistoryEntity> stepHistories = new ArrayList<>();
}