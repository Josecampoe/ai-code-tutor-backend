package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO returned to the frontend after creating or fetching a project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String programmingLanguage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Only the owner's basic info — not the full User object
    private Long userId;
    private String username;
}