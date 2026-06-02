package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.entity.Skill;
import com.yazikochesalna.userservice.data.repository.SkillRepository;
import com.yazikochesalna.userservice.dto.notificationdto.NotificationDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.exception.UserAlreadyExistsCustomException;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserRequestDto;
import com.yazikochesalna.userservice.dto.updateuserdto.UpdateUserResponseDto;
import com.yazikochesalna.userservice.service.mapper.UploadUserMapper;
import com.yazikochesalna.userservice.service.mapper.UsernameNotificationDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProfileDataService {

    private final UsersRepository usersRepository;
    private final UploadUserMapper uploadUserMapper;
    private final MessagingClientService messagingClientService;
    private final SkillRepository skillRepository;

    public void SendUsernameNotification(Long id, UpdateUserRequestDto updateDto) throws ServiceUnavailableException {

        if (updateDto.getUsername() != null){
            NotificationDto notification = UsernameNotificationDtoMapper.
                    convertUpdateUserRequestDtoToNotificationDto(id, updateDto.getUsername());
            messagingClientService.setNewUsername(notification);
        }
    }

    @Transactional
    public UpdateUserResponseDto updateUserProfile(Long id, UpdateUserRequestDto updateDto) {

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException(
                        String.format("Пользователь с %d не найден", id)));

        updateUserFields(user, updateDto);
        updateUserSkills(user, updateDto);
        Users updatedUser = usersRepository.save(user);

        return uploadUserMapper.toUpdateUserResponseDto(updatedUser);
    }

    private void updateUserFields(Users user, UpdateUserRequestDto updateDto) {
        if (updateDto.getUsername() != null) {
            validateUsernameUniqueness(updateDto.getUsername(), user.getId());
            user.setUsername(updateDto.getUsername());
            user.setLow_username(updateDto.getUsername().toLowerCase());
        }
        Optional.ofNullable(updateDto.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(updateDto.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(updateDto.getMiddleName()).ifPresent(user::setMiddleName);
        Optional.ofNullable(updateDto.getPhone()).ifPresent(user::setPhone);
        Optional.ofNullable(updateDto.getDescription()).ifPresent(user::setDescription);
        Optional.ofNullable(updateDto.getBirthDate()).ifPresent(user::setBirthDate);

        Optional.ofNullable(updateDto.getSpecialization()).ifPresent(user::setSpecialization);
        Optional.ofNullable(updateDto.getExperience()).ifPresent(user::setExperience);
    }

    private void updateUserSkills(Users user, UpdateUserRequestDto updateDto) {
        if (updateDto.getSkills() == null) {
            return;
        }

        // Изчистваме старите връзки в колекцията
        user.getSkills().clear();

        // Обхождаме сета от ID-та и намираме съответните управлявани субекти (Skill) от базата
        updateDto.getSkills().forEach(skillId -> {
            if (skillId != null) {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new ResourceNotFoundCustomException(
                                String.format("Навык с id %d не найден в справочнике", skillId)));
                user.getSkills().add(skill);
            }
        });
    }

    private void validateUsernameUniqueness(String username, Long userId) {
        if (usersRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new UserAlreadyExistsCustomException("Такой username уже существует");
        }
    }
}
