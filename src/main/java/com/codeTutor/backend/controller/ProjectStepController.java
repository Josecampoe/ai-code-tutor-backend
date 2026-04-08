package com.codeTutor.backend.controller;

import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.model.ProjectStep;
import com.codeTutor.backend.repository.ProjectRepository;
import com.codeTutor.backend.repository.ProjectStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing project steps.
 * Allows creating steps and marking them as completed.
 */
@RestController
@RequestMapping("/api/projects/{projectId}/steps")
@CrossOrigin(origins = "*")
public class ProjectStepController {

    @Autowired
    private ProjectStepRepository stepRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // GET /api/projects/{projectId}/steps — list all steps for a project
    @GetMapping
    public ResponseEntity<List<ProjectStep>> getSteps(@PathVariable Long projectId) {
        return ResponseEntity.ok(stepRepository.findByProjectIdOrderByStepNumberAsc(projectId));
    }

    // POST /api/projects/{projectId}/steps — create a new step
    @PostMapping
    public ResponseEntity<ProjectStep> createStep(@PathVariable Long projectId,
                                                   @RequestBody ProjectStep step) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + projectId));
        step.setProject(project);
        step.setIsCompleted(false);
        return ResponseEntity.status(HttpStatus.CREATED).body(stepRepository.save(step));
    }

    // PATCH /api/projects/{projectId}/steps/{stepId}/complete — mark step as done
    @PatchMapping("/{stepId}/complete")
    public ResponseEntity<ProjectStep> completeStep(@PathVariable Long stepId) {
        ProjectStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Paso no encontrado: " + stepId));
        step.setIsCompleted(true);
        step.setCompletedAt(LocalDateTime.now());
        return ResponseEntity.ok(stepRepository.save(step));
    }
}
