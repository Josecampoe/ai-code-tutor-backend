package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for creating a new project.
 * Received from the frontend when a user starts a new project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    // The user's description: "I want to build a calculator"
    @NotBlank(message = "Project description is required")
    private String description;

    @NotBlank(message = "Programming language is required")
    private String programmingLanguage;

    // The user who owns this project
    @NotNull(message = "User ID is required")
    private Long userId;
}