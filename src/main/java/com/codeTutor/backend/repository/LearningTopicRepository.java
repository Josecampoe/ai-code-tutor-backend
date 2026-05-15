package com.codeTutor.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.LearningTopic;

@Repository
public interface LearningTopicRepository extends JpaRepository<LearningTopic, Long> {

    // Buscar temas por categoría (DATA_STRUCTURE, DESIGN_PATTERN, ALGORITHM)
    List<LearningTopic> findByCategory(String category);

    // Buscar temas por dificultad
    List<LearningTopic> findByDifficulty(String difficulty);

    // Buscar tema por nombre exacto
    Optional<LearningTopic> findByName(String name);

    Optional<LearningTopic> findByNameContainingIgnoreCase(String name);
}
