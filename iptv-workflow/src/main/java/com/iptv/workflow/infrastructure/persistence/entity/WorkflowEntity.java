// File: myprj6/workflow-service/src/main/java/com/workflow/infrastructure/persistence/entity/WorkflowEntity.java
package com.iptv.workflow.infrastructure.persistence.entity;

import com.workflow.common.event.IPTVStepType;
import com.iptv.workflow.domain.model.workflow.WorkflowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
   @Enumerated(EnumType.STRING)
   @CollectionTable(
           name = "workflow_active_steps",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   private Set<IPTVStepType> activeSteps = EnumSet.noneOf(IPTVStepType.class);

   @ElementCollection
   @Enumerated(EnumType.STRING)
   @CollectionTable(
           name = "workflow_completed_steps",
           joinColumns = @JoinColumn(name = "workflow_id")
   )
   private Set<IPTVStepType> completedSteps = EnumSet.noneOf(IPTVStepType.class);

   @CreationTimestamp
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<StepHistoryEntity> stepHistories = new ArrayList<>();
}