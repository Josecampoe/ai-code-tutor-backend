package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Stores the result of an AI analysis performed on a code snippet.
 * Records what explanation and suggestions were generated for the user.
 */
@Entity
@Table(name = "analysis_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The code that was analyzed
    @Column(columnDefinition = "TEXT", nullable = false)
    private String analyzedCode;

    // AI-generated explanation of the code
    @Column(columnDefinition = "TEXT")
    private String explanation;

    // AI-generated suggestions for next steps
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    // Many analysis records belong to one project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @PrePersist
    protected void onCreate() {
        this.analyzedAt = LocalDateTime.now();
    }
}