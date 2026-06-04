package org.strongcat.vitrinaservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        log.info("CDC: checking Debezium connector status...");
        String connectorUrl = debeziumApiUrl + "/" + connectorName;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(connectorUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: connector '{}' already exists, updating config.", connectorName);
                updateConnectorConfig();
                return;
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.info("CDC: connector '{}' not found, registering it.", connectorName);
            registerConnector();
        } catch (Exception e) {
            log.error("CDC: failed to contact Debezium API.", e);
        }
    }

    private void updateConnectorConfig() {
        String connectorConfigUrl = debeziumApiUrl + "/" + connectorName + "/config";

        try {
            restTemplate.put(connectorConfigUrl, createConnectorConfig());
            log.info("CDC: connector '{}' config updated.", connectorName);
        } catch (Exception e) {
            log.error("CDC: failed to update Debezium connector config.", e);
        }
    }

    private void registerConnector() {
        Map<String, Object> requestBody = Map.of(
                "name", connectorName,
                "config", createConnectorConfig()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(debeziumApiUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: Debezium connector '{}' registered.", connectorName);
            }
        } catch (Exception e) {
            log.error("CDC: failed to register Debezium connector.", e);
        }
    }

    private Map<String, Object> createConnectorConfig() {
        return Map.ofEntries(
                Map.entry("connector.class", "io.debezium.connector.postgresql.PostgresConnector"),
                Map.entry("tasks.max", "1"),
                Map.entry("plugin.name", "pgoutput"),
                Map.entry("database.hostname", "postgres-users"),
                Map.entry("database.port", "5432"),
                Map.entry("database.user", "user"),
                Map.entry("database.password", "1234"),
                Map.entry("database.dbname", "userdatabase"),
                Map.entry("topic.prefix", "cdc"),
                Map.entry("database.history.kafka.bootstrap.servers", "kafka_message_storage:29092"),
                Map.entry("table.include.list", "public.users,public.skill,public.users_skills")
        );
    }
}
