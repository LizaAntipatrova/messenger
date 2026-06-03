package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "d_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_key")
    private Long taskKey;

    @Column(name = "id_natural_task")
    private Integer idNaturalTask;

    @Column(name = "budget")
    private Float budget;

    @Column(name = "experience")
    private Float experience;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "duration")
    private Float duration;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "link_task_skill",
        joinColumns = @JoinColumn(name = "task_key"),
        inverseJoinColumns = @JoinColumn(name = "skill_key")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SkillDimension> skills = new HashSet<>();
}