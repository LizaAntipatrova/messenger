package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "d_skill")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDimension {

    @Id
    @Column(name = "skill_key")
    private Long skillKey;

    @Column(name = "skill_name", length = 1024)
    private String skillName;
}
