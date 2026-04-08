package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Records commands executed in the integrated terminal for a project.
 */
@Entity
@Table(name = "terminal_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TerminalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String command;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        this.executedAt = LocalDateTime.now();
    }
}
