package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import com.yazikochesalna.userservice.dto.personalprofiledto.PersonalProfileDto;
import com.yazikochesalna.userservice.dto.UserProfileDto;
import com.yazikochesalna.userservice.exception.ResourceNotFoundCustomException;
import com.yazikochesalna.userservice.service.mapper.PersonalProfileMapper;
import com.yazikochesalna.userservice.service.mapper.UserProfileDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UsersRepository usersRepository;
    private final AuthorizationClientService authorizationClientService;
    private final PersonalProfileMapper personalProfileMapper;
    private final UserProfileDTOMapper userProfileDTOMapper;

    public UserProfileDto findUserProfile(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User not found with id: " + id));

        return userProfileDTOMapper.toUserProfileDTO(user);
    }

    public PersonalProfileDto findPersonalProfileDTO(Long id)
            throws ServiceUnavailableException{

        Users user = findUser(id);
        String login = authorizationClientService.getUserLogin(id);

        PersonalProfileDto profileDTO = personalProfileMapper.toPersonalProfileDTO(user);
        profileDTO.setLogin(login);
        return profileDTO;
    }

    private Users findUser (Long id){
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User not found with id: " + id));
        return user;
    }

}

