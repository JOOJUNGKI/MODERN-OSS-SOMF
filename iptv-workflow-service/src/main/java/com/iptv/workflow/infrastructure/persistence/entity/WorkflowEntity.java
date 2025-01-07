package com.iptv.workflow.infrastructure.persistence.entity;

import com.iptv.workflow.domain.model.workflow.WorkflowStatus;
import com.workflow.common.persistence.converter.StepTypeStrategyConverter;
import com.workflow.common.step.StepTypeStrategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_iptv_workflow")
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

   @ElementCollection(targetClass = StepTypeStrategy.class)
   @CollectionTable(
           name = "tbl_iptv_workflow_active_step",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   @Column(name = "step_type")
   @Convert(converter = StepTypeStrategyConverter.class)
   private Set<StepTypeStrategy> activeSteps = new HashSet<>();

   @ElementCollection(targetClass = StepTypeStrategy.class)
   @CollectionTable(
           name = "tbl_iptv_workflow_completed_step",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   @Column(name = "step_type")
   @Convert(converter = StepTypeStrategyConverter.class)
   private Set<StepTypeStrategy> completedSteps = new HashSet<>();

   @CreationTimestamp
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<StepHistoryEntity> stepHistories = new ArrayList<>();
}