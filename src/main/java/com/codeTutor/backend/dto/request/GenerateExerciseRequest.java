package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request para generar un ejercicio de práctica con IA.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateExerciseRequest {

    @NotNull(message = "El ID del tema es obligatorio")
    private Long topicId;

    @NotBlank(message = "El lenguaje es obligatorio")
    private String language;

    // userId opcional — para registrar progreso al generar
    private Long userId;
}
