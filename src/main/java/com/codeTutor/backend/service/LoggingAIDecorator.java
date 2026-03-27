package com.codeTutor.backend.service;

/**
 * Decorador que agrega logging a cada llamada al servicio de IA.
 * Registra el tiempo de respuesta y los parámetros de cada operación
 * sin modificar la lógica del AIService original.
 *
 * Patrón Decorator: envuelve un AIServiceInterface y agrega comportamiento
 * de logging antes y después de cada llamada al servicio real.
 */
public class LoggingAIDecorator implements AIServiceInterface {

    // Servicio envuelto — puede ser AIService u otro decorador
    private final AIServiceInterface wrapped;

    /**
     * Constructor que recibe el servicio a decorar.
     */
    public LoggingAIDecorator(AIServiceInterface wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para explicar código.
     */
    @Override
    public String explainCode(String code, String language) {
        // Registrar inicio de la operación con timestamp
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] explainCode llamado | lenguaje: " + language
                + " | código (primeros 50 chars): " + code.substring(0, Math.min(50, code.length())));

        // Delegar al servicio real
        String result = wrapped.explainCode(code, language);

        // Registrar tiempo de respuesta
        System.out.println("[AI-LOG] explainCode completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para sugerir el siguiente paso.
     */
    @Override
    public String suggestNextStep(String code, String language) {
        // Registrar inicio de la operación
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] suggestNextStep llamado | lenguaje: " + language);

        // Delegar al servicio real
        String result = wrapped.suggestNextStep(code, language);

        // Registrar tiempo de respuesta
        System.out.println("[AI-LOG] suggestNextStep completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para generar la guía.
     */
    @Override
    public String generateProjectGuide(String projectDescription) {
        // Registrar inicio de la operación
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] generateProjectGuide llamado | descripción: "
                + projectDescription.substring(0, Math.min(80, projectDescription.length())));

        // Delegar al servicio real
        String result = wrapped.generateProjectGuide(projectDescription);

        // Registrar tiempo de respuesta
        System.out.println("[AI-LOG] generateProjectGuide completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para generar el enunciado del ejercicio.
     */
    @Override
    public String generateExerciseStatement(String topicName, String category, String language) {
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] generateExerciseStatement llamado | tema: " + topicName + " | lenguaje: " + language);
        String result = wrapped.generateExerciseStatement(topicName, category, language);
        System.out.println("[AI-LOG] generateExerciseStatement completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para generar el código de inicio.
     */
    @Override
    public String generateStarterCode(String topicName, String language) {
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] generateStarterCode llamado | tema: " + topicName + " | lenguaje: " + language);
        String result = wrapped.generateStarterCode(topicName, language);
        System.out.println("[AI-LOG] generateStarterCode completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para generar una pista.
     */
    @Override
    public String generateHint(String exerciseStatement, String language) {
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] generateHint llamado | lenguaje: " + language);
        String result = wrapped.generateHint(exerciseStatement, language);
        System.out.println("[AI-LOG] generateHint completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }

    /**
     * Loggea la llamada y delega al servicio envuelto para evaluar la solución del estudiante.
     */
    @Override
    public String evaluateSolution(String exerciseStatement, String solutionCode, String language) {
        long start = System.currentTimeMillis();
        System.out.println("[AI-LOG] evaluateSolution llamado | lenguaje: " + language);
        String result = wrapped.evaluateSolution(exerciseStatement, solutionCode, language);
        System.out.println("[AI-LOG] evaluateSolution completado en " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }
}
