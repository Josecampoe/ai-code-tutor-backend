package com.codeTutor.backend.dto.response;

import lombok.*;
import java.util.List;

/**
 * Full project context loaded when a student resumes a project.
 * Includes current code, steps, and last chat messages.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectContextResponse {

    private Long projectId;
    private String projectName;
    private String description;
    private String language;
    private String currentCode;
    private Integer currentStep;

    private List<StepSummary> steps;
    private List<MessageSummary> lastMessages;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StepSummary {
        private Integer stepNumber;
        private String title;
        private Boolean isCompleted;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MessageSummary {
        private String role;
        private String content;
    }
}
