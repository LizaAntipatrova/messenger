package org.strongcat.vitrinaservice.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "d_date")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateDimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_key")
    private Long dateKey;

    @Column(name = "full_date")
    private LocalDate fullDate;

    @Column(name = "day_of_week")
    private Short dayOfWeek;

    @Column(name = "month_number")
    private Short monthNumber;
}