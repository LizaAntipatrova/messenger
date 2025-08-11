package com.yazikochesalna.messagingservice;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class MessagingServiceApplicationTests {
    //TODO: как сделаю норм конфиг, снесу
    @MockitoBean
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
    }

}
