package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta con los datos de un tema de aprendizaje.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningTopicResponse {

    private Long id;
    private String name;
    private String category;
    private String description;
    private String difficulty;
}
