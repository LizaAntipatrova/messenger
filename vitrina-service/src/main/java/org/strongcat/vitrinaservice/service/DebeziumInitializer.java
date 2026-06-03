package org.strongcat.vitrinaservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebeziumInitializer implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${debezium.api.url}")
    private String debeziumApiUrl;

    @Value("${debezium.connector.name}")
    private String connectorName;

    @Override
    public void run(String... args) {
        log.info("CDC: Проверка статуса коннектора Debezium...");
        String connectorUrl = debeziumApiUrl + "/" + connectorName;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(connectorUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: Коннектор '{}' уже зарегистрирован в Debezium. Повторная настройка не требуется.",
                        connectorName);
                return;
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.info("CDC: Коннектор '{}' не найден. Инициализация регистрации...", connectorName);
            registerConnector();
        } catch (Exception e) {
            log.error("CDC: Не удалось связаться с Debezium API на порту 8090.", e);
        }
    }

    private void registerConnector() {
        Map<String, Object> config = Map.of(
            "connector.class", "io.debezium.connector.postgresql.PostgresConnector",
            "tasks.max", "1",
            "plugin.name", "pgoutput",
            "database.hostname", "postgres-users",
            "database.port", "5432",
            "database.user", "db_admin",
            "database.password", "1234",
            "database.dbname", "users_db",
            "database.topic.prefix", "cdc",
            "table.include.list", "public.users"
        );

        Map<String, Object> requestBody = Map.of(
            "name", connectorName,
            "config", config
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(debeziumApiUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: Коннектор Debezium '{}' успешно зарегистрирован",
                        connectorName);
            }
        } catch (Exception e) {
            log.error("CDC: Ошибка при отправке запроса на регистрацию коннектора в Debezium!", e);
        }
    }
}