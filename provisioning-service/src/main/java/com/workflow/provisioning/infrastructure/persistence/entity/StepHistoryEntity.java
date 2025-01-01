
package com.workflow.provisioning.infrastructure.persistence.entity;

import com.workflow.provisioning.domain.model.lob.LobType;
import com.workflow.provisioning.domain.model.step.StepType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_step_history")
@Getter
@Setter

public class StepHistoryEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "workflow_id")
   private String workflowId;

   @Enumerated(EnumType.STRING)
   private StepType stepType;

   @Column(name = "service_type")
   private String serviceType;

   @Column(name = "order_number")
   private String orderNumber;

   @Column(name = "start_at")
   @CreationTimestamp
   private LocalDateTime startAt;

   @Column(name = "end_at")
   private LocalDateTime endAt;

   @Column(name = "order_sequence")
   private Integer orderSeq;

   @Column(name = "order_type")
   private String orderType;

   @Column(name = "customer_name")
   private String custName;

   @Column(name = "address")
   private String address;

   public void done(){
      this.endAt = LocalDateTime.now();
   }
}
