package org.strongcat.taskservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.strongcat.taskservice.config.properties.LlmProperties;
import org.strongcat.taskservice.dto.internal.LlmProfileResponse;
import org.strongcat.taskservice.dto.internal.LlmTaskResponse;
import org.strongcat.taskservice.dto.internal.OpenAiChatCompletionRequest;
import org.strongcat.taskservice.dto.internal.OpenAiChatCompletionResponse;

import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final WebClient llmWebClient;
    private final LlmProperties properties;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            Ты анализируешь текст в соответствии с инструкциями
            Ты должен присылать в ответе только json, структура которого указана в промпте
            Следуй инструкциям пользователя.
            Если не знаешь ответ, пришли пустой json.
            """;
    private final static String PROFILE_PROMPT = """
            По предлагаемому ниже описанию профиля специалиста выдели основные параметры: опыт работы в месяцах, минимальную желаемую стоимость за проект (в рублях). Если нет упоминания оговоренных параметров, возвращай null.
            Также тебе необходимо вернуть навыки (взять из словаря), которыми владеет специалист, и вес этого навыка (от 0 до 1). Вес показывает текущий уровень владения навыком:(примеры оценки каждого навыка 0.1 -стажер, 0.2 -junior, 0.6 -middle, 0.9 - sinior с опытом работы с технологией больше 10 лет). Вес может быть от 0 до 1 с сотыми долями и должен оцениваться по контексту профиля
            
            В ответе ты должен вернуть только json с заполненными полями:
            {
              "experience_months": 24,
              "min_rate_rub": 150000,
              "skills": [
                {
                  "name": "Python",
                  "weight": 0.8
                }
              ]
            }
            
            Описание профиля специалиста:
            %s
            
            Словарь навыков (имена навыков в ответе должны быть только из этого словаря):
            %s
            """;
    private final static String TASK_PROMPT = """
            По предлагаемому ниже описанию  задачи выдели основные параметры: тебуемый опыт в месяцах, оплата в рублях, ожидаемая длительность выполнения задачи в днях. Если нет упоминания оговоренных параметров, возвращай null. Также тебе необходимо вернуть навыки (взять из словаря), требуемые для выполнения задачи и веса этого навыка(от 0 до 1). вес определяет насколько глубокое знание данной области необходимо для задачи(примеры оценки каждого навыка 0.1 -стажер, 0.2 -junior, 0.6 -middle, 0.9 - sinior с опытом работы с технологией больше 10 лет). Вес может быть от 0 до 1 с сотыми долями и должен оцениваться по контексту задачи
              В ответе ты должен вернуть только json с заполненными знаниями:
              {
                "required_experience_months": 36,
                "payment": 100000,
                "expected_duration_days": null,
                "skills": [
                  {
                    "name": "Spring Boot",
                    "weight": 0.54
                  }
                ]
              }
              Описание задачи:
              %s
              Словарь навыков( имена навыков в ответе должны быть только из этого словаря):
              %s
            """;



@SneakyThrows
    public LlmProfileResponse analyzeProfile(String profile, List<String> skillNames) {
        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest(
                properties.getModel(),
                List.of(
                        new OpenAiChatCompletionRequest.Message(
                                "system",
                                SYSTEM_PROMPT
                        ),
                        new OpenAiChatCompletionRequest.Message(
                                "user",
                                PROFILE_PROMPT.formatted(profile, String.join(",", skillNames))
                )),
                properties.getTemperature(),
                properties.getMaxTokens(),
                false
        );

        OpenAiChatCompletionResponse response = llmWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiChatCompletionResponse.class)
                .block();



        OpenAiChatCompletionResponse.Choice firstChoice = response.choices().get(0);


        String content = firstChoice.message().content();

        log.info(content);
        return objectMapper.readValue(content.trim(), LlmProfileResponse.class);
    }
    @SneakyThrows
    public LlmTaskResponse analyzeTask(String task, List<String> skillNames) {
        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest(
                properties.getModel(),
                List.of(
                        new OpenAiChatCompletionRequest.Message(
                                "system",
                                SYSTEM_PROMPT
                        ),
                        new OpenAiChatCompletionRequest.Message(
                                "user",
                                TASK_PROMPT.formatted(task, String.join(",", skillNames))
                        )),
                properties.getTemperature(),
                properties.getMaxTokens(),
                false
        );
        log.info(objectMapper.writeValueAsString(request));

        OpenAiChatCompletionResponse response = llmWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiChatCompletionResponse.class)
                .block();


        OpenAiChatCompletionResponse.Choice firstChoice = response.choices().get(0);


        String content = firstChoice.message().content();

        log.info(content);
        return objectMapper.readValue(content.trim(), LlmTaskResponse.class);
    }
}
