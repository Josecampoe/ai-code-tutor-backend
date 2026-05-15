package com.codeTutor.backend.controller;

import com.codeTutor.backend.dto.request.EvaluateSolutionRequest;
import com.codeTutor.backend.dto.request.GenerateExerciseRequest;
import com.codeTutor.backend.dto.response.EvaluationResponse;
import com.codeTutor.backend.dto.response.ExerciseResponse;
import com.codeTutor.backend.dto.response.LearningTopicResponse;
import com.codeTutor.backend.dto.response.StudentProgressResponse;
import com.codeTutor.backend.service.LearningService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el módulo "Aprende con IA".
 * Expone endpoints para explorar temas, generar ejercicios,
 * obtener pistas, evaluar soluciones y consultar el progreso del estudiante.
 */
@RestController
@RequestMapping("/api/learn")
public class LearningController {

    @Autowired
    private LearningService learningService;

    // =========================================================
    // TEMAS DE APRENDIZAJE
    // =========================================================

    /**
     * GET /api/learn/topics
     * Retorna todos los temas de aprendizaje disponibles.
     */
    @GetMapping("/topics")
    public ResponseEntity<List<LearningTopicResponse>> getAllTopics() {
        return ResponseEntity.ok(learningService.getAllTopics());
    }

    /**
     * GET /api/learn/topics/category/{category}
     * Retorna los temas filtrados por categoría: DATA_STRUCTURE, DESIGN_PATTERN, ALGORITHM
     */
    @GetMapping("/topics/category/{category}")
    public ResponseEntity<List<LearningTopicResponse>> getTopicsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(learningService.getTopicsByCategory(category));
    }

    // =========================================================
    // EJERCICIOS
    // =========================================================

    /**
     * POST /api/learn/exercises/generate
     * Genera un ejercicio nuevo con IA para el tema y lenguaje solicitados.
     */
    @PostMapping("/exercises/generate")
    public ResponseEntity<ExerciseResponse> generateExercise(@Valid @RequestBody GenerateExerciseRequest request) {
        ExerciseResponse response = learningService.generateExercise(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/learn/exercises/topic/{topicId}/language/{language}
     * Retorna los ejercicios disponibles para un tema y lenguaje específicos.
     */
    @GetMapping("/exercises/topic/{topicId}/language/{language}")
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @PathVariable Long topicId,
            @PathVariable String language) {
        return ResponseEntity.ok(learningService.getExercisesByTopicAndLanguage(topicId, language));
    }

    /**
     * GET /api/learn/exercises/{exerciseId}/hint
     * Retorna una pista para el ejercicio sin revelar la solución.
     */
    @GetMapping("/exercises/{exerciseId}/hint")
    public ResponseEntity<String> getHint(@PathVariable Long exerciseId) {
        return ResponseEntity.ok(learningService.getHint(exerciseId));
    }

    // =========================================================
    // EVALUACIÓN DE SOLUCIONES
    // =========================================================

    /**
     * POST /api/learn/exercises/evaluate
     * Evalúa la solución del estudiante con IA y actualiza su progreso.
     */
    @PostMapping("/exercises/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateSolution(@Valid @RequestBody EvaluateSolutionRequest request) {
        return ResponseEntity.ok(learningService.evaluateSolution(request));
    }

    // =========================================================
    // PROGRESO DEL ESTUDIANTE
    // =========================================================

    /**
     * GET /api/learn/progress/{userId}
     * Retorna el progreso completo del estudiante en todos los temas.
     */
    @GetMapping("/progress/{userId}")
    public ResponseEntity<List<StudentProgressResponse>> getStudentProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(learningService.getStudentProgress(userId));
    }

    /**
     * GET /api/learn/progress/{userId}/topic/{topicId}
     * Retorna el progreso del estudiante en un tema específico.
     */
    @GetMapping("/progress/{userId}/topic/{topicId}")
    public ResponseEntity<StudentProgressResponse> getProgressByTopic(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        return ResponseEntity.ok(learningService.getProgressByTopic(userId, topicId));
    }
}
