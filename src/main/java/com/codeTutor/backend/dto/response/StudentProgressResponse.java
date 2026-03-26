package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Respuesta con el progreso del estudiante en un tema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgressResponse {

    private Long id;
    private Long userId;
    private Long topicId;
    private String topicName;
    private String topicCategory;
    private Integer exercisesCompleted;
    private String status;
    private LocalDateTime lastActivity;
}
