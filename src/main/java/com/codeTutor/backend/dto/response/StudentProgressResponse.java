package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta con el progreso del estudiante en un tema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgressResponse {

    private Long topicId;
    private String topicName;
    private String topicCategory;
    private Integer completedExercises;   // frontend espera "completedExercises"
    private Integer totalExercises;       // frontend espera "totalExercises"
    private String status;
}
