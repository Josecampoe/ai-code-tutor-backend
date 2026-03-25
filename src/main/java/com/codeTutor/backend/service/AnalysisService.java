package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.AnalyzeCodeRequest;
import com.codeTutor.backend.dto.response.AnalysisHistoryResponse;
import com.codeTutor.backend.model.AnalysisHistory;
import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.repository.AnalysisHistoryRepository;
import com.codeTutor.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que coordina el análisis de código con IA y persiste los resultados
 * en el historial de análisis del proyecto.
 * Actúa como puente entre AnalysisFacade y la base de datos.
 */
@Service
public class AnalysisService {

    // Fachada que coordina los servicios de IA y análisis de código
    @Autowired
    private AnalysisFacade analysisFacade;

    // Repositorio para persistir el historial de análisis
    @Autowired
    private AnalysisHistoryRepository analysisHistoryRepository;

    // Repositorio de proyectos para verificar existencia
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Analiza el código del estudiante usando la IA, persiste el resultado
     * en el historial del proyecto y retorna la respuesta completa.
     */
    public AnalysisHistoryResponse analyzeAndSave(AnalyzeCodeRequest request) {
        // Verificar que el proyecto existe antes de analizar
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));

        // Usar la fachada para obtener explicación y sugerencia de la IA
        CodeAnalysisService.AnalysisResult result = analysisFacade.analyzeAndExplain(
                request.getCode(),
                request.getLanguage()
        );

        // Persistir el resultado del análisis en la base de datos
        AnalysisHistory history = AnalysisHistory.builder()
                .analyzedCode(request.getCode())
                .explanation(result.getExplanation())
                .suggestions(result.getSuggestion())
                .project(project)
                .build();

        AnalysisHistory saved = analysisHistoryRepository.save(history);
        return toResponse(saved);
    }

    /**
     * Retorna el historial completo de análisis de un proyecto, del más reciente al más antiguo.
     */
    public List<AnalysisHistoryResponse> getHistoryByProject(Long projectId) {
        // Verificar que el proyecto existe
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + projectId);
        }

        return analysisHistoryRepository.findByProjectIdOrderByAnalyzedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna los últimos 5 análisis de un proyecto.
     */
    public List<AnalysisHistoryResponse> getRecentHistory(Long projectId) {
        return analysisHistoryRepository.findTop5ByProjectIdOrderByAnalyzedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Genera una guía de proyecto usando la IA sin persistirla.
     */
    public String generateGuide(String projectDescription) {
        return analysisFacade.getProjectGuide(projectDescription);
    }

    /**
     * Convierte un AnalysisHistory a su DTO de respuesta.
     */
    private AnalysisHistoryResponse toResponse(AnalysisHistory history) {
        return AnalysisHistoryResponse.builder()
                .id(history.getId())
                .analyzedCode(history.getAnalyzedCode())
                .explanation(history.getExplanation())
                .suggestions(history.getSuggestions())
                .analyzedAt(history.getAnalyzedAt())
                .projectId(history.getProject().getId())
                .build();
    }
}
