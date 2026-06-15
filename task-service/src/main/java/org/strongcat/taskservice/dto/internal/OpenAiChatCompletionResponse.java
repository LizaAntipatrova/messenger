package org.strongcat.taskservice.dto.internal;

import java.util.List;

public record OpenAiChatCompletionResponse(
        String id,
        String object,
        Long created,
        String model,
        List<Choice> choices
) {
    public record Choice(
            Integer index,
            Message message,

            // У разных совместимых API может называться finish_reason
            String finish_reason
    ) {
    }

    public record Message(
            String role,
            String content
    ) {
    }
}
