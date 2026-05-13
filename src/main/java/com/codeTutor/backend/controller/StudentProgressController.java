package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.response.StudentProgressResponse;
import com.codeTutor.backend.service.LearningService;

/**
 * REST controller for the student_progress table.
 * Tracks how many exercises each student has completed per topic.
 */
@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class StudentProgressController {

    @Autowired
    private LearningService learningService;

    /** GET /api/progress — Returns empty completed topics list (placeholder) */
    @GetMapping
    public ResponseEntity<List<String>> getProgress() {
        return ResponseEntity.ok(List.of());
    }

    /** GET /api/progress/{userId} — Get full progress for a student across all topics */
    @GetMapping("/{userId}")
    public ResponseEntity<List<StudentProgressResponse>> getStudentProgress(
            @PathVariable Long userId) {
        return ResponseEntity.ok(learningService.getStudentProgress(userId));
    }

    /** GET /api/progress/{userId}/topic/{topicId} — Get progress for a specific topic */
    @GetMapping("/{userId}/topic/{topicId}")
    public ResponseEntity<StudentProgressResponse> getProgressByTopic(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        return ResponseEntity.ok(learningService.getProgressByTopic(userId, topicId));
    }
}
