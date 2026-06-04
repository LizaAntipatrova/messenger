package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "specialist_skill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @Column(name = "skill_weight", nullable = false)
    private BigDecimal skillWeight;
}
