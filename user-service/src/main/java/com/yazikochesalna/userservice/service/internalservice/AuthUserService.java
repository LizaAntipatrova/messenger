package com.yazikochesalna.userservice.service.internalservice;

import com.yazikochesalna.userservice.exception.UserAlreadyExistsCustomException;
import com.yazikochesalna.userservice.exception.ValidationCustomException;
import com.yazikochesalna.userservice.data.entity.Users;
import com.yazikochesalna.userservice.data.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final UsersRepository usersRepository;

    public List<Long> findUsersIdsByIds(List<Long> ids) {
        return usersRepository.findIdsByIdIn(ids);
    }

    public Users createUser(String username) {

        if (usersRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsCustomException("Username already exists");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setLow_username(username.toLowerCase());

        return usersRepository.save(user);
    }

}
