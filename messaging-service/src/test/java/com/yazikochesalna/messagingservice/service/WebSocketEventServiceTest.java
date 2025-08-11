package com.yazikochesalna.messagingservice.service;

import com.yazikochesalna.messagingservice.dto.events.AwaitingResponseEventDTO;
import com.yazikochesalna.messagingservice.dto.events.EventType;
import com.yazikochesalna.messagingservice.dto.events.payload.chat.impl.ChatMessagePayloadDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WebSocketEventServiceTest {

    @MockitoBean
    private ChatServiceClient chatServiceClient;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @MockitoBean
    private WebSocketSessionService webSocketSessionService;

    @Autowired
    private WebSocketEventService eventService;

    @Test
    public void testSendMessage_UserInChat() {
        WebSocketSession session = mock(WebSocketSession.class);
        AwaitingResponseEventDTO eventDTO = new AwaitingResponseEventDTO();
        eventDTO.setType(EventType.MESSAGE);
        eventDTO.setPayload(new ChatMessagePayloadDTO());
        Long userId = 1L;
        Long chatId = 2L;

        when(webSocketSessionService.getUserId(session)).thenReturn(userId);
        when(chatServiceClient.isUserInChat(userId, chatId)).thenReturn(true);

        eventService.sendMessage(session, eventDTO);

        verify(kafkaProducerService).sendMessage(any(), any());
    }

    @Test
    public void testSendMessage_UserNotInChat() {
        WebSocketSession session = mock(WebSocketSession.class);
        AwaitingResponseEventDTO eventDTO = new AwaitingResponseEventDTO();
        eventDTO.setType(EventType.MESSAGE);
        eventDTO.setPayload(new ChatMessagePayloadDTO());
        eventDTO.setRequestId(1L);
        Long userId = 1L;
        Long chatId = 2L;

        when(webSocketSessionService.getUserId(session)).thenReturn(userId);
        when(chatServiceClient.isUserInChat(userId, chatId)).thenReturn(false);

        eventService.sendMessage(session, eventDTO);

        verify(webSocketSessionService).getConcurrentSession(session);
        verify(kafkaProducerService, never()).sendMessage(any(), any());
    }
}
