package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta de la IA al evaluar la solución del estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    // Si la solución es correcta o no
    private boolean correct;

    // Retroalimentación detallada de la IA
    private String feedback;

    // Sugerencia de mejora (si aplica)
    private String improvement;

    // Progreso actualizado del estudiante
    private Integer exercisesCompleted;
}
