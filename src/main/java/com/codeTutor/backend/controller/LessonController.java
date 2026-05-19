package com.codeTutor.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.codeTutor.backend.service.LessonService;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
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
    public ResponseEntity<Lesson> getLessonByTopicAndLevel(
            @PathVariable Long topicId,
            @RequestParam String level,
            @RequestParam Integer lessonNumber) {
        return lessonService.findByTopicIdAndLevelAndLessonNumber(topicId, level, lessonNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/topic/{topicId}/level/{level}")
    public ResponseEntity<List<Lesson>> getLessonsByTopicAndLevel(
            @PathVariable Long topicId,
            @PathVariable String level) {
        List<Lesson> lessons = lessonService.findByTopicIdAndLevel(topicId, level);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/debug/count")
    public ResponseEntity<?> debugCount() {
        List<Lesson> all = lessonService.findAll();
        Map<String, Object> body = new HashMap<>();
        body.put("total", all.size());
        List<Map<String, Object>> samples = all.stream()
            .limit(3)
            .map(l -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", l.getId().toString());
                m.put("title", l.getTitle());
                m.put("level", l.getLevel());
                m.put("lessonNumber", l.getLessonNumber());
                m.put("topicName", l.getTopic().getName());
                return m;
            })
            .collect(Collectors.toList());
        body.put("samples", samples);
        return ResponseEntity.ok(body);
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
}
