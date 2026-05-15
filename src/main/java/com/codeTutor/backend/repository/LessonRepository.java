package com.codeTutor.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    Optional<Lesson> findByTopicIdAndLanguageAndLevelAndLessonNumber(Long topicId, String language, String level, Integer lessonNumber);

    List<Lesson> findByTopicIdAndLevelOrderByLessonNumber(Long topicId, String level);
}
