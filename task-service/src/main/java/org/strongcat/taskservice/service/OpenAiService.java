package org.strongcat.taskservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.strongcat.taskservice.dto.internal.ChatRequest;
import org.strongcat.taskservice.dto.internal.LlmProfileResponse;
import org.strongcat.taskservice.dto.internal.LlmTaskResponse;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClient client;
    private final ObjectMapper objectMapper;
    @Value("${openai.model}")
    private String model;
    private static final String SYSTEM_PROMPT = """
            Ты анализируешь текст в соответствии с инструкциями
            Ты должен присылать в ответе только json, структура которого указана в промпте
            Следуй инструкциям пользователя.
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
        ChatRequest request = new ChatRequest(
                SYSTEM_PROMPT,
                PROFILE_PROMPT.formatted(profile, String.join(", ", skillNames)),
                0.0);

    return objectMapper.readValue( chat(request), LlmProfileResponse.class);
    }

    private String chat(ChatRequest request) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .temperature(request.temperature())
                .addSystemMessage(request.systemPrompt())
                .addUserMessage(request.userPrompt())
                .build();

        ChatCompletion completion = client.chat()
                .completions()
                .create(params);

        String answer = completion.choices()
                .get(0)
                .message()
                .content()
                .orElse("");
        log.info(answer);
        return answer;
    }

    @SneakyThrows
    public LlmTaskResponse analyzeTask(String taskDescription, List<String> skillNames) {
        ChatRequest request = new ChatRequest(
                SYSTEM_PROMPT,
                TASK_PROMPT.formatted(taskDescription, String.join(", ", skillNames)),
                0.7);

        return objectMapper.readValue( chat(request), LlmTaskResponse.class);
    }
}
