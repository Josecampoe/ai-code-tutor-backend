package com.codeTutor.backend.controller;

import com.codeTutor.backend.dto.request.RunCodeRequest;
import com.codeTutor.backend.dto.response.RunCodeResponse;
import com.codeTutor.backend.service.CodeExecutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the integrated terminal.
 * Executes student code and returns stdout, stderr and exit code.
 */
@RestController
@RequestMapping("/api/terminal")
public class TerminalController {

    @Autowired
    private CodeExecutionService codeExecutionService;

    /**
     * POST /api/terminal/run
     * Executes the submitted code and returns the output.
     * Body: { code, language, stdin (optional) }
     */
    @PostMapping("/run")
    public ResponseEntity<RunCodeResponse> run(@Valid @RequestBody RunCodeRequest request) {
        RunCodeResponse response = codeExecutionService.execute(request);
        return ResponseEntity.ok(response);
    }
}
