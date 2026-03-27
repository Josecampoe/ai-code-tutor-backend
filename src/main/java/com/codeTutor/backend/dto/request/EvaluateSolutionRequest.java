package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request para que la IA evalúe la solución del estudiante a un ejercicio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluateSolutionRequest {

    @NotNull(message = "El ID del ejercicio es obligatorio")
    private Long exerciseId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "El código es obligatorio")
    private String userCode;          // frontend manda "userCode"

    @NotBlank(message = "El lenguaje es obligatorio")
    private String language;
}
