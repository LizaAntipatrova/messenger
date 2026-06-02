package com.yazikochesalna.userservice.data.entity;

import com.yazikochesalna.userservice.component.UserEntityListener;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@EntityListeners(UserEntityListener.class)
@Table(name = "users")
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    private static final String phoneRegular = "^\\+?[0-9\\s\\-()]{7,20}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    @Column(name = "low_username", length = 50, unique = true, nullable = false)
    @NotBlank(message = "low_username cannot be blank")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String low_username;

    @Column(name = "file_uuid")
    private UUID fileUuid;

    @Column(name = "last_name", length = 50)
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Column(name = "first_name", length = 50)
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Column(name = "middle_name", length = 50)
    @Size(max = 50, message = "Middle name must be less than 50 characters")
    private String middleName;

    @Column(name = "phone", length = 20)
    @Pattern(regexp = phoneRegular, message = "Phone number is invalid")
    private String phone;

    @Column(name = "birth_date")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Column(name = "specialization", length = 100)
    @Size(max = 100, message = "Specialization must be less than 100 characters")
    private String specialization;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

}
