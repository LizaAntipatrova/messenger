package com.yazikochesalna.userservice.service.mapper;

import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UploadUserMapper {

    @Mapping(target = "userId", source = "id")
    UpdateUserResponseDto toUpdateUserResponseDto(Users user);
}
