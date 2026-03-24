package com.codeTutor.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "*")
public class CodeController {

    // POST /api/code/analyze
    // Receives the student's code and returns an explanation
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeCode(@RequestBody String code) {
        return ResponseEntity.ok("Code received: " + code);
    }

    // POST /api/code/suggest
    // Receives the student's code and returns next step suggestions
    @PostMapping("/suggest")
    public ResponseEntity<String> getSuggestions(@RequestBody String code) {
        return ResponseEntity.ok("Suggestions received for: " + code);
    }

}