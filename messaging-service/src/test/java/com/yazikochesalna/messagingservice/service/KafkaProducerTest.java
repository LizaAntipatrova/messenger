package com.yazikochesalna.messagingservice.service;

import com.yazikochesalna.messagingservice.dto.events.EventDTO;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class KafkaProducerTest {
    //TODO: как сделаю норм конфиг, снесу
    @MockitoBean
    private RedissonClient redissonClient;

    @MockitoBean
    private KafkaTemplate<String, EventDTO> kafkaTemplate;

    @Autowired
    private KafkaProducerService kafkaProducerService;


    @Test
    public void testSendMessage() {
        EventDTO event = new EventDTO();

        kafkaProducerService.sendMessage(event);

        verify(kafkaTemplate).send("messages", event);
    }

}
