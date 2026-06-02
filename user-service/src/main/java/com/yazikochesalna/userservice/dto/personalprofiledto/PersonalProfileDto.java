package com.yazikochesalna.userservice.dto.personalprofiledto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalProfileDto {

    private long userId;

    private String username;

    private String login;

    private UUID fileUuid;
    private String lastName;
    private String firstName;
    private String middleName;
    private String phone;
    private String description;
    private LocalDate birthDate;

    private String specialization;
    private Integer experience;
    private Set<SkillDto> skills;

}
