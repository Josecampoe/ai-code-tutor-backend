package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for saving a new code snapshot (version of the code).
 * Received from the frontend when the user saves their progress.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCodeSnapshotRequest {

    // The actual code content to save
    @NotBlank(message = "Code content is required")
    private String content;

    // Optional label the user gives to this version
    private String versionLabel;

    // The project this snapshot belongs to
    @NotNull(message = "Project ID is required")
    private Long projectId;
}