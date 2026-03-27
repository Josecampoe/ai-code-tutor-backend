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

    private boolean correct;
    private String feedback;
    private Integer score;            // 0-100, calculado según la evaluación de la IA
}
