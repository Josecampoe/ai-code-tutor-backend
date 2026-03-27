package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta del tutor IA al mensaje del estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private String message;
}
