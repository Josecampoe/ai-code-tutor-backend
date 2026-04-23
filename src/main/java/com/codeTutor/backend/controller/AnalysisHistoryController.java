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

import com.codeTutor.backend.dto.request.AnalyzeCodeRequest;
import com.codeTutor.backend.dto.response.AnalysisHistoryResponse;
import com.codeTutor.backend.service.AnalysisService;

import jakarta.validation.Valid;

/**
 * REST controller for the analysis_history table.
 * Manages AI code analysis records for each project.
 */
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisHistoryController {

    @Autowired
    private AnalysisService analysisService;

    /** POST /api/analysis — Analyze code with AI and save the result */
    @PostMapping
    public ResponseEntity<AnalysisHistoryResponse> analyzeCode(
            @Valid @RequestBody AnalyzeCodeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analysisService.analyzeAndSave(request));
    }

    /** GET /api/analysis/project/{projectId} — Get full analysis history for a project */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AnalysisHistoryResponse>> getHistoryByProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(analysisService.getHistoryByProject(projectId));
    }

    /** GET /api/analysis/project/{projectId}/recent — Get last 5 analyses */
    @GetMapping("/project/{projectId}/recent")
    public ResponseEntity<List<AnalysisHistoryResponse>> getRecentHistory(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(analysisService.getRecentHistory(projectId));
    }
}
