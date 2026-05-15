package com.codeTutor.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.Lesson;
import com.codeTutor.backend.repository.LessonRepository;

@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> findById(UUID id) {
        return lessonRepository.findById(id);
    }

    public Optional<Lesson> findByTopicIdAndLanguageAndLevelAndLessonNumber(Long topicId, String language, String level, Integer lessonNumber) {
        return lessonRepository.findByTopicIdAndLanguageAndLevelAndLessonNumber(topicId, language, level, lessonNumber);
    }

    public List<Lesson> findByTopicIdAndLevel(Long topicId, String level) {
        return lessonRepository.findByTopicIdAndLevelOrderByLessonNumber(topicId, level);
    }

    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Optional<Lesson> update(UUID id, Lesson updatedLesson) {
        return lessonRepository.findById(id)
                .map(existingLesson -> {
                    existingLesson.setTopic(updatedLesson.getTopic());
                    existingLesson.setLanguage(updatedLesson.getLanguage());
                    existingLesson.setLevel(updatedLesson.getLevel());
                    existingLesson.setLessonNumber(updatedLesson.getLessonNumber());
                    existingLesson.setTitle(updatedLesson.getTitle());
                    existingLesson.setSummary(updatedLesson.getSummary());
                    existingLesson.setContentJson(updatedLesson.getContentJson());
                    existingLesson.setEstimatedMinutes(updatedLesson.getEstimatedMinutes());
                    return lessonRepository.save(existingLesson);
                });
    }

    public boolean delete(UUID id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
