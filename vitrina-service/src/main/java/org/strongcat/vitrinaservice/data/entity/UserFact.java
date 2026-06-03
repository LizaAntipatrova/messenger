package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fact_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fact")
    private Long idFact;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_key", nullable = false)
    private UserDimension userDimension;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_key", nullable = false)
    private TaskDimension taskDimension;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "date_key", nullable = false)
    private DateDimension dateDimension;
}