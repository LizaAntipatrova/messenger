package org.strongcat.vitrinaservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.strongcat.vitrinaservice.data.entity.UserAnalyticsView;
import org.strongcat.vitrinaservice.data.repository.UserAnalyticsViewRepository;
import org.strongcat.vitrinaservice.dto.UserAnalyticsDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalyticsService {

    private final UserAnalyticsViewRepository userAnalyticsViewRepository;

    public Optional<UserAnalyticsDto> getUserAnalytics(Integer idNaturalUser) {
        return userAnalyticsViewRepository.findByIdNaturalUser(idNaturalUser)
                .map(this::convertToDto);
    }

    private UserAnalyticsDto convertToDto(UserAnalyticsView view) {
        return UserAnalyticsDto.builder()
                .idNaturalUser(view.getIdNaturalUser())
                .averagePayment(view.getAveragePayment())
                .preferedComplexity(view.getPreferedComplexity())
                .lastActivityDate(view.getLastActivityDate())
                .taskExperienceHours(view.getTaskExperienceHours())
                .build();
    }
}