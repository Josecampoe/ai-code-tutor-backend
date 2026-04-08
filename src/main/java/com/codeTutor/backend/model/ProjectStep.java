package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a step in the student's project development guide.
 * Steps are suggested by the AI and tracked for completion.
 */
@Entity
@Table(name = "project_steps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "suggested_by_ai")
    private Boolean suggestedByAi = true;
}
