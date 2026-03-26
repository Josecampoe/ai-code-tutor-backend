package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Respuesta con los datos de un ejercicio generado por la IA.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseResponse {

    private Long id;
    private String statement;
    private String starterCode;
    private String language;
    private Long topicId;
    private String topicName;
    private LocalDateTime createdAt;
}
