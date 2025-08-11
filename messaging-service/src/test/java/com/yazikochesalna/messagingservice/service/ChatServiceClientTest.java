package com.yazikochesalna.messagingservice.service;

import com.yazikochesalna.common.service.JwtService;
import com.yazikochesalna.messagingservice.config.properties.ChatServiceProperties;
import com.yazikochesalna.messagingservice.dto.chat.UsersResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//TODO: имеет ли смысл
@SpringBootTest
public class ChatServiceClientTest {
    //TODO: как сделаю норм конфиг, снесу
    @MockitoBean
    private RedissonClient redissonClient;

    @MockitoBean
    private WebClient chatServiceWebClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ChatServiceProperties chatServiceProperties;

    @Autowired
    private ChatServiceClient chatServiceClient;
    @BeforeEach
    public void setup() {
        String token = "test-token";
        when(chatServiceProperties.getUrl()).thenReturn("http://test-chat-service-url");
        when(jwtService.generateServiceToken()).thenReturn(token);

    }

    @Test
    public void testIsUserInChat() {
        Long userId = 1L;
        Long chatId = 2L;

        WebClient.RequestHeadersUriSpec requestSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(chatServiceWebClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(new org.springframework.http.ResponseEntity<>(HttpStatus.OK)));


        boolean result = chatServiceClient.isUserInChat(userId, chatId);

        assertEquals(true, result);
    }

    @Test
    public void testGetUsersByChatId() {
        Long chatId = 2L;
        List<Long> userIds = List.of(1L, 2L);
        UsersResponseDTO responseDTO = new UsersResponseDTO(userIds);

        WebClient.RequestHeadersUriSpec requestSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(chatServiceWebClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UsersResponseDTO.class)).thenReturn(Mono.just(responseDTO));

        List<Long> result = chatServiceClient.getUsersByChatId(chatId);

        assertEquals(userIds, result);
    }
}
