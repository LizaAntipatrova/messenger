package com.yazikochesalna.userservice.dto.notificationdto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserPayloadDto extends PayloadDto {
    @NotNull(message = "userId не может быть null")
    protected Long userId;

}
