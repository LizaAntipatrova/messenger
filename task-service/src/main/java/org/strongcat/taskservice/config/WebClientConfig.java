package org.strongcat.taskservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.strongcat.taskservice.config.properties.LlmProperties;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    @Value("${messaging.service.url}")
    private String messagingServiceUrl;
    private final LlmProperties llmProperties;

    @Value("${webclient.timeout:5000}")
    private Integer webClientTimeout;

    private static final int timeoutSeconds = 60;

    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientTimeout)
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    public WebClient messagingServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(messagingServiceUrl)
                .build();
    }

    @Bean
    public WebClient llmWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(llmProperties.getBaseUrl())
                .build();
    }
}
