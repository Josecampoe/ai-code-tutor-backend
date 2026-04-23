package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a saved version of the code at a specific point in time.
 * Used to implement version history (Linked List structure concept).
 * Each snapshot is a node that points to the previous version.
 */
@Entity
@Table(name = "code_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The actual code content saved at this point
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Optional label: "added loop", "fixed bug"
    @Column(name = "version_label")
    private String versionLabel;

    // Version number within the project (1, 2, 3...)
    @Column(name = "version_number")
    private Integer versionNumber;

    // Reason this snapshot was created
    @Column(name = "snapshot_reason")
    private String snapshotReason = "autosave"; // autosave, manual_save, step_completed, session_start

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Many snapshots belong to one project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}