package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.request.EvaluateSolutionRequest;
import com.codeTutor.backend.dto.request.GenerateExerciseRequest;
import com.codeTutor.backend.dto.response.EvaluationResponse;
import com.codeTutor.backend.dto.response.ExerciseResponse;
import com.codeTutor.backend.service.LearningService;

import jakarta.validation.Valid;

/**
 * REST controller for the exercises table.
 * Manages AI-generated exercises, hints and solution evaluation.
 */
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private LearningService learningService;

    /** POST /api/exercises/generate — Generate a new exercise with AI */
    @PostMapping("/generate")
    public ResponseEntity<ExerciseResponse> generateExercise(
            @Valid @RequestBody GenerateExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningService.generateExercise(request));
    }

    /** GET /api/exercises/topic/{topicId}/language/{language} — List exercises by topic and language */
    @GetMapping("/topic/{topicId}/language/{language}")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByTopicAndLanguage(
            @PathVariable Long topicId,
            @PathVariable String language) {
        return ResponseEntity.ok(learningService.getExercisesByTopicAndLanguage(topicId, language));
    }

    /** GET /api/exercises/{exerciseId}/hint — Get a hint without revealing the solution */
    @GetMapping("/{exerciseId}/hint")
    public ResponseEntity<String> getHint(@PathVariable Long exerciseId) {
        return ResponseEntity.ok(learningService.getHint(exerciseId));
    }

    /** POST /api/exercises/evaluate — Evaluate student solution with AI */
    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateSolution(
            @Valid @RequestBody EvaluateSolutionRequest request) {
        return ResponseEntity.ok(learningService.evaluateSolution(request));
    }
}
