package com.yazikochesalna.messagingservice.service;

import com.yazikochesalna.messagingservice.exception.InvalidWebSocketTokenCustomException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RedissonWebSocketTokenServiceTest {

    @MockitoBean
    private RedissonClient redissonClient;

    @MockitoBean
    private RBucket<Object> bucket;
    @Autowired
    private RedissonWebSocketTokenService tokenService;

    @Test
    public void testGenerateToken() {
        Long userId = 1L;

        when(redissonClient.getBucket(anyString())).thenReturn(bucket);

        String token = tokenService.generateToken(userId);

        verify(bucket).set(userId, Duration.ofMinutes(5));
        assertNotNull(token);
    }

    @Test
    public void testValidateAndGetUserId_ValidToken() {
        String token = "valid-token";
        Long userId = 1L;

        when(redissonClient.getBucket(anyString())).thenReturn(bucket);
        when(bucket.isExists()).thenReturn(true);
        when(bucket.getAndDelete()).thenReturn(userId);

        Long result = tokenService.validateAndGetUserId(token);

        assertEquals(userId, result);
        verify(bucket).getAndDelete();
    }

    @Test
    public void testValidateAndGetUserId_InvalidToken() {
        String token = "invalid-token";

        when(redissonClient.getBucket(anyString())).thenReturn(bucket);
        when(bucket.isExists()).thenReturn(false);

        assertThrows(InvalidWebSocketTokenCustomException.class, () -> tokenService.validateAndGetUserId(token));
    }
}
