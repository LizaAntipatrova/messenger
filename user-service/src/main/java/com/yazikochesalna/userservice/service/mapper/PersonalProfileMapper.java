//package com.yazikochesalna.userservice.service.mapper;
//
//import com.yazikochesalna.userservice.data.entity.Users;
//import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface PersonalProfileMapper {
//
//    @Mapping(target = "login", ignore = true)
//    @Mapping(target = "userId", source = "id")
//    PersonalProfileDto toPersonalProfileDto(Users user);
//}

package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.data.entity.Skill;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
import com.yazikochesalna.userservice.dto.personalprofiledto.SkillDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonalProfileMapper {

    @Mapping(target = "login", ignore = true)
    @Mapping(target = "userId", source = "id")
    PersonalProfileDto toPersonalProfileDto(Users user);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "low_username", ignore = true)
    Users toUsers(PersonalProfileDto personalProfileDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "low_username", ignore = true)
    void updateUsersFromDto(PersonalProfileDto dto, @MappingTarget Users user);

    // Маппинг одиночного навыка в DTO
    SkillDto skillToSkillDto(Skill skill);

    // Маппинг DTO в одиночную сущность навыка
    Skill skillDtoToSkill(SkillDto skillDto);
}