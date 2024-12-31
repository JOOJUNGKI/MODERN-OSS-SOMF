
package com.workflow.provisioning.infrastructure.persistence.entity;

import com.workflow.provisioning.domain.model.lob.LobType;
import com.workflow.provisioning.domain.model.step.StepType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

   @Enumerated(EnumType.STRING)
   private StepType stepType;

   @Column(name = "lob")
   @Enumerated(EnumType.STRING)
   private LobType lob;

   @Column(name = "order_number")
   private String orderNumber;

   @Column(name = "start_at")
   @CreatedDate
   private LocalDateTime startAt;

   @Column(name = "end_at")
   private LocalDateTime endAt;

   public void done(){
      this.endAt = LocalDateTime.now();
   }
}
