package org.strongcat.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.strongcat.taskservice.data.repository.SkillRepository;
import org.strongcat.taskservice.data.repository.SpecializationRepository;
import org.strongcat.taskservice.dto.ReferenceItemDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final SpecializationRepository specializationRepository;
    private final SkillRepository skillRepository;

    @GetMapping("/specializations")
    public List<ReferenceItemDto> getSpecializations() {
        return specializationRepository.findAll().stream()
                .map(item -> new ReferenceItemDto(item.getId(), item.getName()))
                .toList();
    }

    @GetMapping("/skills")
    public List<ReferenceItemDto> getSkills() {
        return skillRepository.findAll(Pageable.ofSize(20)).stream()
                .map(item -> new ReferenceItemDto(item.getId(), item.getName()))
                .toList();
    }
}
