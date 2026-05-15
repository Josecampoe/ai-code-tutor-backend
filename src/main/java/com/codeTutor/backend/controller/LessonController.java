package com.codeTutor.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable UUID id) {
        return lessonService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<?> getLessonByTopicAndLanguageAndLevel(
            @PathVariable Long topicId,
            @RequestParam String language,
            @RequestParam String level,
            @RequestParam Integer lessonNumber) {
        var existing = lessonService.findByTopicIdAndLanguageAndLevelAndLessonNumber(topicId, language, level, lessonNumber);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }

        try {
            var topic = learningTopicRepository.findById(topicId);
            if (topic.isEmpty()) return ResponseEntity.notFound().build();

            String topicName = topic.get().getName();

            String lessonTitle = getLessonTitle(level, lessonNumber);
            String contentJson = aiService.generateLessonContent(topicName, language, level);
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
                    .lessonNumber(lessonNumber)
                    .title(lessonTitle)
                    .summary("Lesson " + lessonNumber + " of " + level + " level for " + topicName)
                    .contentJson(contentJson)
                    .estimatedMinutes(10)
                    .build();

            Lesson saved = lessonService.save(lesson);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating lesson: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lesson));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable UUID id, @RequestBody Lesson lesson) {
        return lessonService.update(id, lesson)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        return lessonService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private String getLessonTitle(String level, int lessonNumber) {
        String[] beginnerTitles = {
            "Introduction", "Variables and Data Types", "Operators",
            "Control Flow", "Loops", "Functions",
            "Arrays and Lists", "Strings", "Introduction to OOP",
            "Beginner Project"
        };
        String[] intermediateTitles = {
            "Object-Oriented Programming Deep Dive", "Error Handling",
            "Collections and Data Structures", "File I/O",
            "Modules and Packages", "Functional Programming Basics",
            "Recursion", "Interfaces and Abstractions",
            "Generics and Templates", "Intermediate Project"
        };
        String[] advancedTitles = {
            "Design Patterns in Practice", "Concurrency and Threads",
            "Memory Management", "Testing",
            "Performance and Optimization", "Advanced OOP",
            "Working with APIs", "Databases and Persistence",
            "Deployment Basics", "Advanced Project"
        };

        String[] titles;
        switch (level) {
            case "intermediate" -> titles = intermediateTitles;
            case "advanced" -> titles = advancedTitles;
            default -> titles = beginnerTitles;
        }

        if (lessonNumber >= 1 && lessonNumber <= 10) {
            return titles[lessonNumber - 1];
        }
        return "Lesson " + lessonNumber;
    }
}
