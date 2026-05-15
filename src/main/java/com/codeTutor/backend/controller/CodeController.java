package com.codeTutor.backend.controller;

import com.codeTutor.backend.dto.request.AnalyzeCodeRequest;
import com.codeTutor.backend.dto.response.AnalysisHistoryResponse;
import com.codeTutor.backend.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el análisis de código con IA.
 * Expone endpoints para analizar código, obtener sugerencias e historial de análisis.
 */
@RestController
@RequestMapping("/api/code")
public class CodeController {

    // Servicio de análisis inyectado
    @Autowired
    private AnalysisService analysisService;

    /**
     * POST /api/code/analyze
     * Analiza el código del estudiante con IA y guarda el resultado en el historial.
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisHistoryResponse> analyzeCode(
            @Valid @RequestBody AnalyzeCodeRequest request) {
        AnalysisHistoryResponse response = analysisService.analyzeAndSave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/code/guide
     * Genera una guía paso a paso para el proyecto descrito por el estudiante.
     */
    @PostMapping("/guide")
    public ResponseEntity<String> generateGuide(@RequestBody String projectDescription) {
        String guide = analysisService.generateGuide(projectDescription);
        return ResponseEntity.ok(guide);
    }

    /**
     * GET /api/code/history/{projectId}
     * Retorna el historial completo de análisis de un proyecto.
     */
    @GetMapping("/history/{projectId}")
    public ResponseEntity<List<AnalysisHistoryResponse>> getHistory(@PathVariable Long projectId) {
        List<AnalysisHistoryResponse> response = analysisService.getHistoryByProject(projectId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/code/history/{projectId}/recent
     * Retorna los últimos 5 análisis de un proyecto.
     */
    @GetMapping("/history/{projectId}/recent")
    public ResponseEntity<List<AnalysisHistoryResponse>> getRecentHistory(@PathVariable Long projectId) {
        List<AnalysisHistoryResponse> response = analysisService.getRecentHistory(projectId);
        return ResponseEntity.ok(response);
    }
}
