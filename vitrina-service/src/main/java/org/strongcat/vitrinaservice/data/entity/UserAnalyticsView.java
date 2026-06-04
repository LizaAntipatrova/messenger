package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;

import java.time.LocalDate;

@Entity
@Table(name = "mv_user_analytics")
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAnalyticsView {

    @Id
    @Column(name = "id_natural_user")
    private Integer idNaturalUser;

    @Column(name = "prefered_complexity")
    private Float preferedComplexity;

    @Type(IntArrayType.class)
    @Column(name = "top_task_category", columnDefinition = "integer[]")
    private int[] topTaskCategory;

    @Column(name = "task_experience_hours")
    private Float taskExperienceHours;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "average_payment")
    private Float averagePayment;
}