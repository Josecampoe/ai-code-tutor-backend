package com.codeTutor.backend.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Decorador que agrega caché a las llamadas al servicio de IA.
 * Si el mismo código ya fue analizado antes, retorna la respuesta cacheada
 * sin hacer una nueva llamada a la API, ahorrando tiempo y cuota.
 *
 * Patrón Decorator: envuelve un AIServiceInterface y agrega comportamiento
 * de caché transparente sin modificar el servicio original.
 */
public class CachingAIDecorator implements AIServiceInterface {

    // Servicio envuelto — puede ser AIService, LoggingAIDecorator u otro decorador
    private final AIServiceInterface wrapped;

    /**
     * Caché de explicaciones: clave = código+lenguaje, valor = explicación generada.
     * Se usa HashMap por su acceso O(1), ideal para búsquedas frecuentes de caché.
     */
    private final Map<String, String> explanationCache = new HashMap<>();

    /**
     * Caché de sugerencias: clave = código+lenguaje, valor = sugerencia generada.
     */
    private final Map<String, String> suggestionCache = new HashMap<>();

    /**
     * Constructor que recibe el servicio a decorar.
     */
    public CachingAIDecorator(AIServiceInterface wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Retorna la explicación desde caché si existe, o llama al servicio y la cachea.
     */
    @Override
    public String explainCode(String code, String language) {
        // Construir la clave única para este código y lenguaje
        String cacheKey = buildKey(code, language);

        // Verificar si ya existe en caché
        if (explanationCache.containsKey(cacheKey)) {
            System.out.println("[AI-CACHE] Hit en explanationCache para lenguaje: " + language);
            return explanationCache.get(cacheKey);
        }

        // No está en caché — llamar al servicio real y guardar el resultado
        System.out.println("[AI-CACHE] Miss en explanationCache — llamando a la IA");
        String result = wrapped.explainCode(code, language);
        explanationCache.put(cacheKey, result);
        return result;
    }

    /**
     * Retorna la sugerencia desde caché si existe, o llama al servicio y la cachea.
     */
    @Override
    public String suggestNextStep(String code, String language) {
        // Construir la clave única para este código y lenguaje
        String cacheKey = buildKey(code, language);

        // Verificar si ya existe en caché
        if (suggestionCache.containsKey(cacheKey)) {
            System.out.println("[AI-CACHE] Hit en suggestionCache para lenguaje: " + language);
            return suggestionCache.get(cacheKey);
        }

        // No está en caché — llamar al servicio real y guardar el resultado
        System.out.println("[AI-CACHE] Miss en suggestionCache — llamando a la IA");
        String result = wrapped.suggestNextStep(code, language);
        suggestionCache.put(cacheKey, result);
        return result;
    }

    /**
     * Las guías de proyecto no se cachean porque cada descripción es única.
     * Delega directamente al servicio envuelto.
     */
    @Override
    public String generateProjectGuide(String projectDescription) {
        return wrapped.generateProjectGuide(projectDescription);
    }

    /**
     * Los enunciados de ejercicios no se cachean — cada generación es única.
     */
    @Override
    public String generateExerciseStatement(String topicName, String category, String language) {
        return wrapped.generateExerciseStatement(topicName, category, language);
    }

    /**
     * Los códigos de inicio no se cachean — cada generación es única.
     */
    @Override
    public String generateStarterCode(String topicName, String language) {
        return wrapped.generateStarterCode(topicName, language);
    }

    /**
     * Las pistas no se cachean — el estudiante puede pedir pistas distintas.
     */
    @Override
    public String generateHint(String exerciseStatement, String language) {
        return wrapped.generateHint(exerciseStatement, language);
    }

    /**
     * Las evaluaciones no se cachean — cada solución del estudiante es diferente.
     */
    @Override
    public String evaluateSolution(String exerciseStatement, String solutionCode, String language) {
        return wrapped.evaluateSolution(exerciseStatement, solutionCode, language);
    }

    /**
     * El chat no se cachea — cada conversación es única y depende del contexto.
     */
    @Override
    public String chat(String message, String conversationHistory, String currentCode, String language) {
        return wrapped.chat(message, conversationHistory, currentCode, language);
    }

    /**
     * Limpia toda la caché — útil cuando el estudiante cambia de proyecto.
     */
    public void clearCache() {
        explanationCache.clear();
        suggestionCache.clear();
        System.out.println("[AI-CACHE] Caché limpiada.");
    }

    /**
     * Construye una clave única combinando código y lenguaje.
     * Usa los primeros 200 caracteres del código para evitar claves muy largas.
     */
    private String buildKey(String code, String language) {
        String codeSnippet = code.substring(0, Math.min(200, code.length()));
        return language.toLowerCase() + "::" + codeSnippet.hashCode();
    }
}
