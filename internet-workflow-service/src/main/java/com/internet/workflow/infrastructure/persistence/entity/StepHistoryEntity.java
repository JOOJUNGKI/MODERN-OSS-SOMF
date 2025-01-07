// File: myprj6/workflow-service/src/main/java/com/workflow/infrastructure/persistence/entity/StepHistoryEntity.java
package com.internet.workflow.infrastructure.persistence.entity;

import com.workflow.common.event.StepType;
import com.workflow.common.persistence.converter.StepTypeStrategyConverter;
import com.workflow.common.step.StepTypeStrategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_internet_step_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepHistoryEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Convert(converter = StepTypeStrategyConverter.class)
   @Column(name = "step_type", nullable = false)
   private StepTypeStrategy stepTypeStrategy;

   @Column(name = "start_time", nullable = false)
   private LocalDateTime startTime;

   @Column(name = "end_time")
   private LocalDateTime endTime;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "workflow_id", nullable = false)
   private WorkflowEntity workflow;

   public void complete() {
      if (this.endTime == null) {
         this.endTime = LocalDateTime.now();
      }
   }

   @PrePersist
   protected void onCreate() {
      if (startTime == null) {
         startTime = LocalDateTime.now();
      }
   }
}