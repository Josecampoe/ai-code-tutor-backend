package com.codeTutor.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.Lesson;
import com.codeTutor.backend.repository.LessonRepository;

/**
 * Service layer for Lesson entity.
 * Handles business logic for lesson operations.
 */
@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    /**
     * Retrieves all lessons.
     */
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    /**
     * Finds a lesson by its ID.
     */
    public Optional<Lesson> findById(UUID id) {
        return lessonRepository.findById(id);
    }

    /**
     * Finds a lesson by topic, language, and level.
     * Returns empty if not found.
     */
    public Optional<Lesson> findByTopicIdAndLanguageAndLevel(UUID topicId, String language, String level) {
        return lessonRepository.findByTopicIdAndLanguageAndLevel(topicId, language, level);
    }

    /**
     * Saves a new lesson.
     */
    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    /**
     * Updates an existing lesson.
     */
    public Optional<Lesson> update(UUID id, Lesson updatedLesson) {
        return lessonRepository.findById(id)
                .map(existingLesson -> {
                    existingLesson.setTopic(updatedLesson.getTopic());
                    existingLesson.setLanguage(updatedLesson.getLanguage());
                    existingLesson.setLevel(updatedLesson.getLevel());
                    existingLesson.setTitle(updatedLesson.getTitle());
                    existingLesson.setSummary(updatedLesson.getSummary());
                    existingLesson.setContentJson(updatedLesson.getContentJson());
                    existingLesson.setEstimatedMinutes(updatedLesson.getEstimatedMinutes());
                    return lessonRepository.save(existingLesson);
                });
    }

    /**
     * Deletes a lesson by its ID.
     */
    public boolean delete(UUID id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
