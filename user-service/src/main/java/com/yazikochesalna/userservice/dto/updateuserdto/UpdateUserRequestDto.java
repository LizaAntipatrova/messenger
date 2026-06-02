package com.yazikochesalna.userservice.dto.updateuserdto;

import com.yazikochesalna.userservice.dto.personalprofiledto.SkillDto;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must be less than 50 characters")
    private String middleName;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Phone number is invalid")
    private String phone;

    @Size(max = 100, message = "Specialization must be less than 100 characters")
    private String specialization;

    private Integer experience;

    private String description;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private Set<Long> skills;
}