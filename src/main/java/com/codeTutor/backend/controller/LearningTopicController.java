package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.response.LearningTopicResponse;
import com.codeTutor.backend.service.LearningService;

/**
 * REST controller for the learning_topics table.
 * Provides access to all available learning topics by category.
 */
@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "*")
public class LearningTopicController {

    @Autowired
    private LearningService learningService;

    /** GET /api/topics — Get all learning topics */
    @GetMapping
    public ResponseEntity<List<LearningTopicResponse>> getAllTopics() {
        return ResponseEntity.ok(learningService.getAllTopics());
    }

    /** GET /api/topics/category/{category} — Filter by DATA_STRUCTURE, DESIGN_PATTERN, ALGORITHM */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<LearningTopicResponse>> getTopicsByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(learningService.getTopicsByCategory(category));
    }
}
