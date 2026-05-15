package com.codeTutor.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.model.Lesson;
import com.codeTutor.backend.repository.LearningTopicRepository;
import com.codeTutor.backend.service.AIServiceInterface;
import com.codeTutor.backend.service.LessonService;

/**
 * REST controller for Lesson operations.
 * Provides endpoints for CRUD operations and lesson retrieval by topic/language/level.
 */
@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final LearningTopicRepository learningTopicRepository;
    private final AIServiceInterface aiService;

    public LessonController(LessonService lessonService, LearningTopicRepository learningTopicRepository, AIServiceInterface aiService) {
        this.lessonService = lessonService;
        this.learningTopicRepository = learningTopicRepository;
        this.aiService = aiService;
    }

    /**
     * GET /api/lessons
     * Retrieves all lessons.
     */
    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        List<Lesson> lessons = lessonService.findAll();
        return ResponseEntity.ok(lessons);
    }

    /**
     * GET /api/lessons/{id}
     * Retrieves a single lesson by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable UUID id) {
        return lessonService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/lessons/topic/{topicId}?language=Java&level=beginner
     * Retrieves a lesson or generates one with AI if it doesn't exist.
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<?> getLessonByTopicAndLanguageAndLevel(
            @PathVariable Long topicId,
            @RequestParam String language,
            @RequestParam String level) {
        // Try to find existing lesson
        var existing = lessonService.findByTopicIdAndLanguageAndLevel(topicId, language, level);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }

        // Generate with AI
        try {
            var topic = learningTopicRepository.findById(topicId);
            if (topic.isEmpty()) return ResponseEntity.notFound().build();

            String topicName = topic.get().getName();
            System.out.println("[LessonController] Generating lesson for: " + topicName + " | " + language + " | " + level);

            String contentJson = aiService.generateLessonContent(topicName, language, level);
            System.out.println("[LessonController] AI response length: " + (contentJson != null ? contentJson.length() : 0));

            // Clean AI response — remove markdown fences if present
            if (contentJson != null) {
                contentJson = contentJson.trim();
                if (contentJson.startsWith("```json")) contentJson = contentJson.substring(7);
                if (contentJson.startsWith("```")) contentJson = contentJson.substring(3);
                if (contentJson.endsWith("```")) contentJson = contentJson.substring(0, contentJson.length() - 3);
                contentJson = contentJson.trim();
            }

            Lesson lesson = Lesson.builder()
                    .topic(topic.get())
                    .language(language)
                    .level(level)
                    .title(topicName + " - " + level)
                    .summary("AI-generated lesson about " + topicName + " in " + language)
                    .contentJson(contentJson)
                    .estimatedMinutes(10)
                    .build();

            Lesson saved = lessonService.save(lesson);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("[LessonController] Error generating lesson: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generating lesson: " + e.getMessage());
        }
    }

    /**
     * POST /api/lessons
     * Creates a new lesson manually.
     */
    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson savedLesson = lessonService.save(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }

    /**
     * PUT /api/lessons/{id}
     * Updates an existing lesson.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable UUID id,
            @RequestBody Lesson lesson) {
        return lessonService.update(id, lesson)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/lessons/{id}
     * Deletes a lesson by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        boolean deleted = lessonService.delete(id);
        return deleted 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
}
