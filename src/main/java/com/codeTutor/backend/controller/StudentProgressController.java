package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.response.StudentProgressResponse;
import com.codeTutor.backend.exception.ForbiddenException;
import com.codeTutor.backend.service.LearningService;

@RestController
@RequestMapping("/api/progress")
public class StudentProgressController {

    @Autowired
    private LearningService learningService;

    @GetMapping
    public ResponseEntity<List<String>> getProgress() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<StudentProgressResponse>> getStudentProgress(
            @PathVariable Long userId, Authentication auth) {
        verifyOwnership(userId, auth);
        return ResponseEntity.ok(learningService.getStudentProgress(userId));
    }

    @GetMapping("/{userId}/topic/{topicId}")
    public ResponseEntity<StudentProgressResponse> getProgressByTopic(
            @PathVariable Long userId,
            @PathVariable Long topicId,
            Authentication auth) {
        verifyOwnership(userId, auth);
        return ResponseEntity.ok(learningService.getProgressByTopic(userId, topicId));
    }

    private void verifyOwnership(Long userId, Authentication auth) {
        if (auth == null || !auth.getPrincipal().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
