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
}
