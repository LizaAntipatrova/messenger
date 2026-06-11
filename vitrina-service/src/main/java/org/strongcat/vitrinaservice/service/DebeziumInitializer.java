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
import org.springframework.util.StringUtils;
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

    @Value("${debezium.task.connector.name}")
    private String taskConnectorName;

    @Override
    public void run(String... args) {
        log.info("CDC: checking Debezium connectors...");
        manageConnector(connectorName, createUserConnectorConfig());

        if (StringUtils.hasText(taskConnectorName)) {
            manageConnector(taskConnectorName, createTaskConnectorConfig());
        }
    }

    private void manageConnector(String connector, Map<String, Object> config) {
        String connectorUrl = debeziumApiUrl + "/" + connector;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(connectorUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: connector '{}' already exists, updating config.", connector);
                updateConnectorConfig(connector, config);
                return;
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.info("CDC: connector '{}' not found, registering it.", connector);
            registerConnector(connector, config);
        } catch (Exception e) {
            log.error("CDC: failed to contact Debezium API for connector {}.", connector, e);
        }
    }

    private void updateConnectorConfig(String connector, Map<String, Object> config) {
        String connectorConfigUrl = debeziumApiUrl + "/" + connector + "/config";
        try {
            restTemplate.put(connectorConfigUrl, config);
            log.info("CDC: connector '{}' config updated.", connector);
        } catch (Exception e) {
            log.error("CDC: failed to update Debezium connector config for {}.", connector, e);
        }
    }

    private void registerConnector(String connector, Map<String, Object> config) {
        Map<String, Object> requestBody = Map.of(
                "name", connector,
                "config", config
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(debeziumApiUrl, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                log.info("CDC: Debezium connector '{}' registered.", connector);
            }
        } catch (Exception e) {
            log.error("CDC: failed to register Debezium connector {}.", connector, e);
        }
    }

    private Map<String, Object> createUserConnectorConfig() {
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


    private Map<String, Object> createTaskConnectorConfig() {
        return Map.ofEntries(
                Map.entry("connector.class", "io.debezium.connector.postgresql.PostgresConnector"),
                Map.entry("tasks.max", "1"),
                Map.entry("plugin.name", "pgoutput"),
                Map.entry("database.hostname", "postgres-task"),
                Map.entry("database.port", "5432"),
                Map.entry("database.user", "user"),
                Map.entry("database.password", "1234"),
                Map.entry("database.dbname", "task-database"),
                Map.entry("topic.prefix", "cdc"),
                Map.entry("decimal.handling.mode", "double"),
                Map.entry("database.history.kafka.bootstrap.servers", "kafka_message_storage:29092"),
                Map.entry("table.include.list","public.request," +
                                "public.request_recipient," +
                                "public.request_skill," +
                                "public.specialist," +
                                "public.request_status," +
                                "public.response_status"
                )
        );
    }

}
