package com.yazikochesalna.userservice.dto.updateuserdto;

import com.yazikochesalna.userservice.dto.personalprofiledto.SkillDto;
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
public class UpdateUserResponseDto {

    private Long userId;
    private String username;
    private String lastName;
    private String firstName;
    private String middleName;
    private String phone;
    private String specialization;
    private Integer experience;
    private String description;
    private LocalDate birthDate;

    private Set<SkillDto> skills;

}