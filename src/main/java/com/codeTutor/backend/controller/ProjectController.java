package com.codeTutor.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    // POST /api/projects
    // Creates a new project for the student
    @PostMapping
    public ResponseEntity<String> createProject(@RequestBody String projectDescription) {
        return ResponseEntity.ok("Project created: " + projectDescription);
    }

    // GET /api/projects/{id}
    // Retrieves a project by its ID
    @GetMapping("/{id}")
    public ResponseEntity<String> getProject(@PathVariable Long id) {
        return ResponseEntity.ok("Project found with id: " + id);
    }

}