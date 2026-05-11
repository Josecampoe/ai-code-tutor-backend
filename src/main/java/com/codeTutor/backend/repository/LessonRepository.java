package com.codeTutor.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.Lesson;

/**
 * Repository interface for Lesson entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    
    /**
     * Finds a lesson by topic, language, and level combination.
     * This combination is unique per the database constraint.
     */
    Optional<Lesson> findByTopicIdAndLanguageAndLevel(UUID topicId, String language, String level);
}
