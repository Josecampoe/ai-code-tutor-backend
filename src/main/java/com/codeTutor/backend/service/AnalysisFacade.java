package com.codeTutor.backend.service;

import com.codeTutor.backend.service.CodeAnalysisService.AnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Fachada principal del sistema de análisis de código.
 * Es el único punto de entrada desde la capa de controladores hacia los servicios internos.
 * Oculta la complejidad de AIService, CodeAnalysisService y ProjectService,
 * exponiendo una interfaz simplificada y coherente para el controlador.
 *
 * Justificación del patrón Facade:
 * Se utiliza Facade porque el sistema tiene múltiples servicios con responsabilidades distintas
 * (IA, análisis de código, gestión de proyectos). Sin esta fachada, el controlador tendría que
 * conocer y coordinar directamente todos esos servicios, generando alto acoplamiento.
 * La fachada centraliza esa coordinación, simplifica el controlador y facilita el mantenimiento:
 * si cambia la lógica interna, solo se modifica la fachada, no el controlador.
 */
@Service
public class AnalysisFacade {

    // Servicio de inteligencia artificial para generación de guías y explicaciones
    @Autowired
    private AIService aiService;

    // Servicio de análisis de código para detectar lenguaje y analizar estructura
    @Autowired
    private CodeAnalysisService codeAnalysisService;

    // Servicio de gestión de proyectos para persistencia y control de versiones
    @Autowired
    private ProjectService projectService;

    /**
     * Analiza el código del estudiante y retorna una explicación completa con sugerencias.
     * Delega el análisis a CodeAnalysisService y retorna el resultado encapsulado.
     */
    public AnalysisResult analyzeAndExplain(String code, String language) {
        // Delegar el análisis completo al servicio especializado de análisis de código
        return codeAnalysisService.analyzeCode(code, language);
    }

    /**
     * Genera una guía paso a paso para que el estudiante desarrolle el proyecto descrito.
     * Delega la generación de la guía al servicio de IA.
     */
    public String getProjectGuide(String projectDescription) {
        // Solicitar a la IA que genere una guía orientada al aprendizaje del estudiante
        return aiService.generateProjectGuide(projectDescription);
    }

    /**
     * Guarda el nuevo código en el proyecto, lo analiza y retorna el resultado del análisis.
     * Coordina ProjectService y CodeAnalysisService en una sola operación para el controlador.
     */
    public AnalysisResult saveAndAnalyze(String projectId, String code, String language) {
        // Convertir el ID del proyecto de String a Long para usarlo en el servicio
        Long id = Long.parseLong(projectId);

        // Actualizar el código del proyecto y guardar la versión anterior en el historial
        projectService.updateProjectCode(id, code);

        // Analizar el nuevo código y retornar el resultado con explicación y sugerencia
        return codeAnalysisService.analyzeCode(code, language);
    }

    /**
     * Deshace la última acción realizada en el editor del proyecto.
     * Delega la operación a ProjectService.
     */
    public void undoLastAction() {
        // Delegar la operación de deshacer al servicio de proyectos
        projectService.undo();
    }

    /**
     * Rehace la última acción deshecha en el editor del proyecto.
     * Delega la operación a ProjectService.
     */
    public void redoLastAction() {
        // Delegar la operación de rehacer al servicio de proyectos
        projectService.redo();
    }

    /**
     * Retorna el historial completo de versiones del código de un proyecto.
     * Delega la consulta a ProjectService.
     */
    public List<String> getVersionHistory(Long projectId) {
        // Delegar la consulta del historial de versiones al servicio de proyectos
        return projectService.getVersionHistory();
    }
}
