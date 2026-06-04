package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "response_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 1024, nullable = false, unique = true)
    private String name;
}
