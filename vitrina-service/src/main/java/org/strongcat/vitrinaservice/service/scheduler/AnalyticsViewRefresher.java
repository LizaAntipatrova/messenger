package org.strongcat.vitrinaservice.service.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.strongcat.vitrinaservice.data.repository.UserAnalyticsViewRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsViewRefresher {

    private final UserAnalyticsViewRepository userAnalyticsViewRepository;

    @Scheduled(fixedRate = 1800000, initialDelay = 10000)
    public void refreshUserAnalyticsView() {
        log.info("ПЛАНИРОВЩИК: Запуск фонового обновления материализованного представления mv_user_analytics...");
        
        try {
            long startTime = System.currentTimeMillis();

            userAnalyticsViewRepository.refreshAnalyticsView();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("ПЛАНИРОВЩИК: Материализованное представление успешно обновлено за {} мс.", duration);
            
        } catch (Exception e) {
            log.error("ПЛАНИРОВЩИК: Ошибка при обновлении материализованного представления!", e);
        }
    }
}