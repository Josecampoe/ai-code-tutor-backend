package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Registra el progreso de un estudiante en un tema de aprendizaje específico.
 * Permite saber cuántos ejercicios completó y cuál es su nivel actual.
 */
@Entity
@Table(name = "student_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cantidad de ejercicios completados en este tema
    @Column(name = "exercises_completed", nullable = false)
    private Integer exercisesCompleted;

    // Estado: IN_PROGRESS, COMPLETED
    @Column(nullable = false)
    private String status;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    // Muchos registros de progreso pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Muchos registros de progreso pertenecen a un tema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private LearningTopic topic;

    @PrePersist
    protected void onCreate() {
        this.lastActivity = LocalDateTime.now();
        if (this.exercisesCompleted == null) this.exercisesCompleted = 0;
        if (this.status == null) this.status = "IN_PROGRESS";
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastActivity = LocalDateTime.now();
    }
}
