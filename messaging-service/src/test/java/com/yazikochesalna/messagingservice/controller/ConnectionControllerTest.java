package com.yazikochesalna.messagingservice.controller;

import com.yazikochesalna.common.authentication.JwtAuthenticationToken;

import com.yazikochesalna.messagingservice.service.RedissonWebSocketTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.matchesRegex;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class ConnectionControllerTest {

    public static final String API_GENERATE_WS_TOKEN = "/api/v1/ws/connect";
    public static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockitoBean
    private RedissonClient redissonClient;

    @MockitoBean
    private RBucket<Object> bucket;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetWebSocketToken() throws Exception {
        Long userId = 1L;

        JwtAuthenticationToken authToken = Mockito.mock(JwtAuthenticationToken.class);
        when(authToken.getUserId()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        when(redissonClient.getBucket(anyString())).thenReturn(bucket);

        mockMvc.perform(post(API_GENERATE_WS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").value(matchesRegex(REGEX_UUID)));
    }
}
