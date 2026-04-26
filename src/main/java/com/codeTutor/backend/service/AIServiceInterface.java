package com.codeTutor.backend.service;

/**
 * Interfaz que define el contrato del servicio de inteligencia artificial.
 * Permite aplicar el patrón Decorator sobre AIService sin modificarlo.
 *
 * Justificación del patrón Decorator:
 * Se utiliza Decorator porque necesitamos agregar comportamientos adicionales
 * (logging, caché) al servicio de IA de forma flexible y sin modificar AIService.
 * Cada decorador envuelve al anterior, formando una cadena de responsabilidades
 * que se puede configurar dinámicamente. Esto respeta el principio Open/Closed:
 * abierto para extensión, cerrado para modificación.
 */
public interface AIServiceInterface {

    /**
     * Explica el código del estudiante de forma clara para principiantes.
     */
    String explainCode(String code, String language);

    /**
     * Sugiere el siguiente paso para continuar el desarrollo.
     */
    String suggestNextStep(String code, String language);

    /**
     * Genera una guía de pasos iniciales para el proyecto descrito.
     */
    String generateProjectGuide(String projectDescription);

    // =========================================================
    // MÉTODOS DEL MÓDULO "APRENDE CON IA"
    // =========================================================

    /**
     * Genera el enunciado de un ejercicio práctico sobre un tema y lenguaje específicos.
     */
    String generateExerciseStatement(String topicName, String category, String language);

    /**
     * Genera un código de inicio (esqueleto) para que el estudiante complete el ejercicio.
     */
    String generateStarterCode(String topicName, String language);

    /**
     * Genera una pista para el ejercicio sin revelar la solución completa.
     */
    String generateHint(String exerciseStatement, String language);

    /**
     * Evalúa la solución del estudiante y retorna retroalimentación detallada.
     */
    String evaluateSolution(String exerciseStatement, String solutionCode, String language);

    /**
     * Responde al mensaje del estudiante en el chat conversacional.
     * Mantiene el contexto del historial y del código actual en el editor.
     */
    String chat(String message, String conversationHistory, String currentCode, String language);

    /**
     * Analiza el código del estudiante y retorna un análisis estructurado en formato JSON.
     * Actúa como tutor pedagógico, guiando sin dar soluciones directas.
     */
    String analyzeCode(String code, String language, String projectDescription);
}
