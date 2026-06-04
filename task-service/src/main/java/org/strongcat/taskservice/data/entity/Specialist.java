package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "specialist")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_user_id", nullable = false, unique = true)
    private Long externalUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    @Column(name = "experience_months", nullable = false)
    private Integer experienceMonths;

    @Column(name = "min_payment", nullable = false)
    private BigDecimal minPayment;

    @Column(name = "avg_task_cost")
    private BigDecimal avgTaskCost;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "preferred_difficulty_months")
    private Integer preferredDifficultyMonths;
}
