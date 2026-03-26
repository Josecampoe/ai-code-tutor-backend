package com.codeTutor.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Representa un tema de aprendizaje disponible en la plataforma.
 * Puede ser una estructura de datos (Stack, Queue, LinkedList...)
 * o un patrón de diseño (Singleton, Observer, Factory...).
 */
@Entity
@Table(name = "learning_topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del tema: "Stack", "Observer Pattern", "Binary Search"
    @Column(nullable = false, unique = true)
    private String name;

    // Categoría: "DATA_STRUCTURE" o "DESIGN_PATTERN" o "ALGORITHM"
    @Column(nullable = false)
    private String category;

    // Descripción breve del tema
    @Column(columnDefinition = "TEXT")
    private String description;

    // Nivel de dificultad: BEGINNER, INTERMEDIATE, ADVANCED
    @Column(nullable = false)
    private String difficulty;

    // Un tema tiene muchos ejercicios
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercise> exercises;

    // Un tema tiene muchos registros de progreso de estudiantes
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentProgress> progressRecords;
}
