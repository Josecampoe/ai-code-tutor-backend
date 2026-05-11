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
import com.codeTutor.backend.service.LessonService;

/**
 * REST controller for Lesson operations.
 * Provides endpoints for CRUD operations and lesson retrieval by topic/language/level.
 */
@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
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
     * Retrieves or generates a lesson for a specific topic, language, and level.
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Lesson> getLessonByTopicAndLanguageAndLevel(
            @PathVariable UUID topicId,
            @RequestParam String language,
            @RequestParam String level) {
        return lessonService.findByTopicIdAndLanguageAndLevel(topicId, language, level)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
