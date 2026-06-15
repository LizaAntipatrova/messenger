package org.strongcat.taskservice.dto.internal;

public record ChatRequest(
        String systemPrompt,
        String userPrompt,
        Double temperature
) {
}
