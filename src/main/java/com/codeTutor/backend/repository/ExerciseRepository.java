package com.codeTutor.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    // Buscar ejercicios por tema
    List<Exercise> findByTopicId(Long topicId);

    // Buscar ejercicios por tema y lenguaje
    List<Exercise> findByTopicIdAndLanguage(Long topicId, String language);
}
