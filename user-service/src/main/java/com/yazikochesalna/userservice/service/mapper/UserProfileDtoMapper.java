//package com.yazikochesalna.userservice.service.mapper;
//
//import com.yazikochesalna.userservice.data.entity.Users;
//import com.yazikochesalna.userservice.dto.UserProfileDto;
//import org.mapstruct.Mapper;
//
//@Mapper(componentModel = "spring")
//public interface UserProfileDtoMapper {
//
//   UserProfileDto toUserProfileDto(Users user);
//
//}

package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.data.entity.Skill;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.dto.UserProfileDto;
import com.yazikochesalna.userservice.dto.personalprofiledto.SkillDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileDtoMapper {

   /**
    * Маппинг сущности пользователя в UserProfileDto (для просмотра чужих профилей)
    */
   UserProfileDto toUserProfileDto(Users user);

   /**
    * Маппинг сущности пользователя в UpdateUserResponseDto (используется в твоем сервисе updateUserProfile)
    */
   @Mapping(target = "userId", source = "id")
   UpdateUserResponseDto toUpdateUserResponseDto(Users user);

   /**
    * Вспомогательный метод: MapStruct автоматически применит его для конвертации
    * каждого элемента внутри коллекции Set<Skill> -> Set<SkillDto>
    */
   SkillDto skillToSkillDto(Skill skill);
}