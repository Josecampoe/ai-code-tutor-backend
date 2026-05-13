package com.codeTutor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an AI-generated lesson for a specific topic, language, and level.
 * Each lesson contains structured content in JSON format.
 */
@Entity
@Table(
    name = "lessons",
    uniqueConstraints = @UniqueConstraint(columnNames = {"topic_id", "language", "level"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "exercises", "lessons", "progressRecords"})
    private LearningTopic topic;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content_json", columnDefinition = "TEXT")
    private String contentJson;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
    }
}
