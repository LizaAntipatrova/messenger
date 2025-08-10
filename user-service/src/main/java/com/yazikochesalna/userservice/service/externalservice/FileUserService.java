package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.dto.fileupdatedto.FileUpdateRequestDto;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.service.mapper.AvatarNotificationDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUserService {

    private final UsersRepository usersRepository;
    private final MessagingClientService messagingClientService;

    public void updateUserFileUuidSendNotification(FileUpdateRequestDto requestDto) throws ServiceUnavailableException {

        updateUserFileUuid(requestDto.getUserId(), requestDto.getFileUuid());

        NotificationDto notification =
                AvatarNotificationDtoMapper.convertFileUpdateRequestDtoToNotificationDto(requestDto);
        messagingClientService.setNewAvatar(notification);
    }

    private void updateUserFileUuid(Long userId, UUID fileUuid) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User not found"));

        usersRepository.updateFileUuid(userId, fileUuid);
    }

}
