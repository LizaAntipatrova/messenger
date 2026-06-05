package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "required_experience")
    private Integer requiredExperience;

    @Column(name = "payment", nullable = false)
    private BigDecimal payment;

    @Column(name = "expected_duration_days")
    private Long expectedDurationDays;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_status_id", nullable = false)
    private RequestStatus requestStatus;
}
