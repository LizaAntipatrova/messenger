package com.yazikochesalna.userservice.dto.checkdto;

import java.util.List;

public record CheckUsersResponseDto(
        List<Long> existingUsersIds
){
}
