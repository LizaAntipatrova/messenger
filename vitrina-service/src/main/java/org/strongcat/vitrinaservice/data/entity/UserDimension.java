package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "d_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Long userKey;

    @Column(name = "id_natural_user")
    private Integer idNaturalUser;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "specialization", length = 50)
    private String specialization;

    @Column(name = "experience")
    private Float experience;

    @Column(name = "prefered_complexity")
    private Float preferedComplexity;

    @Type(IntArrayType.class)
    @Column(name = "top_task_category", columnDefinition = "integer[]")
    private int[] topTaskCategory;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "link_user_skill",
        joinColumns = @JoinColumn(name = "user_key"),
        inverseJoinColumns = @JoinColumn(name = "skill_key")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SkillDimension> skills = new HashSet<>();
}