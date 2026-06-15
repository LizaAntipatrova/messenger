package org.strongcat.taskservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAiChatCompletionRequest(
        String model,
        List<Message> messages,
        Double temperature,

        @JsonProperty("max_tokens")
        Integer maxTokens,

        Boolean stream
) {
    public record Message(
            String role,
            String content
    ) {
    }
}
