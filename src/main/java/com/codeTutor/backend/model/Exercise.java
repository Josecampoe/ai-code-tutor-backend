package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Representa un ejercicio de práctica generado por la IA para un tema específico.
 * Cada ejercicio está asociado a un tema y a un lenguaje de programación.
 */
@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Enunciado del ejercicio generado por la IA
    @Column(columnDefinition = "TEXT", nullable = false)
    private String statement;

    // Código de inicio (esqueleto) que se le da al estudiante
    @Column(columnDefinition = "TEXT")
    private String starterCode;

    // Lenguaje de programación: "java", "python", "javascript", "typescript"
    @Column(nullable = false)
    private String language;

    // Pistas disponibles para el estudiante (separadas por |)
    @Column(columnDefinition = "TEXT")
    private String hints;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Muchos ejercicios pertenecen a un tema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private LearningTopic topic;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
