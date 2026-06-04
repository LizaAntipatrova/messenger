package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "specialization")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 1024, nullable = false, unique = true)
    private String name;
}
