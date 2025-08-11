//package com.yazikochesalna.messagingservice.controller;
//
//import com.yazikochesalna.messagingservice.dto.events.EventDTO;
//import com.yazikochesalna.messagingservice.service.WebSocketEventService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//public class NotificationControllerTest {
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private WebSocketEventService webSocketEventService;
//
//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//    }
//
//    @Test
//    public void testSendUpdateChatMember() throws Exception {
//        // Инициализация MockMvc
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        // Создаем тестовый EventDTO
//        EventDTO eventDTO = new EventDTO();
//        String eventJson = "{\"type\": \"NEW_MEMBER\", \"payload\": {\"chatId\": 1}}";
//
//        // Выполнение запроса и проверка результата
//        mockMvc.perform(post("/api/v1/ws/notification/update-members")
//                        .contentType("application/json")
//                        .content(eventJson))
//                .andExpect(status().isOk());
//
//        // Проверяем, что метод сервиса был вызван
//        verify(webSocketEventService).sendMessage(any(EventDTO.class));
//    }
//}
