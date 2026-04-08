package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/**
 * Request para el chat conversacional con el tutor IA.
 * Incluye el mensaje actual y el historial para mantener contexto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    // Optional: project context for persistent session history
    private Long projectId;
    private Long userId;

    // Fallback history from frontend if no projectId provided
    private List<ChatMessage> history;

    // Código actual en el editor (opcional, para contexto)
    private String currentCode;

    // Lenguaje actual del editor (opcional)
    private String language;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;    // "user" o "ai"
        private String content;
    }
}
