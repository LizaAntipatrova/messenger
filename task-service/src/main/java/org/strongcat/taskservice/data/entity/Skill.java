package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_group_id", nullable = false)
    private SkillGroup skillGroup;

    @Column(name = "name", length = 1024, nullable = false)
    private String name;
}
