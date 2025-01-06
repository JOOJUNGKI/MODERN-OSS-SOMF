package com.workflow.common.persistence.entity;

import com.workflow.common.event.StepType;
import com.workflow.common.persistence.converter.StepTypeStrategyConverter;
import com.workflow.common.step.ServiceType;
import com.workflow.common.step.StepTypeStrategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class StepHistoryEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_id")
    private String workflowId;

    @Convert(converter = StepTypeStrategyConverter.class)
    @Column(name = "step_type", nullable = false)
    private StepTypeStrategy stepType;

    @Column(name = "service_type")
    private ServiceType serviceType;

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

