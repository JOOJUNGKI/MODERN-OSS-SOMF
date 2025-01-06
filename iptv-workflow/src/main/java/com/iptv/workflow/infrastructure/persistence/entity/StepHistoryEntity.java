package com.iptv.workflow.infrastructure.persistence.entity;

import com.workflow.common.persistence.converter.StepTypeStrategyConverter;
import com.workflow.common.step.StepTypeStrategy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_iptv_step_histories")
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