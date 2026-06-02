package com.yazikochesalna.userservice.service.externalservice;

import com.yazikochesalna.userservice.data.repository.SkillRepository;
import com.yazikochesalna.userservice.dto.personalprofiledto.SkillDto;
import com.yazikochesalna.userservice.service.mapper.UserProfileDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserProfileDtoMapper userProfileDtoMapper;

    @Transactional(readOnly = true)
    public List<SkillDto> getAllSkills() {
        log.info("Fetching all skills from database ordered by name and mapping to DTO");

        return skillRepository.findAllByOrderByNameAsc().stream()
                .map(userProfileDtoMapper::skillToSkillDto)
                .collect(Collectors.toList());
    }
}