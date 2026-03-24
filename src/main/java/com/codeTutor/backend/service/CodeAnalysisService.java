package com.codeTutor.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Servicio de análisis de código que detecta el lenguaje, analiza la estructura del código
 * y coordina explicaciones y sugerencias mediante la IA.
 *
 * Implementa dos patrones de diseño:
 * - Factory Method: para crear el analizador correcto según el lenguaje detectado.
 * - Observer: para notificar automáticamente a los componentes interesados cuando el código cambia.
 */
@Service
public class CodeAnalysisService implements CodeChangeObserver {

    /**
     * Justificación del patrón Factory Method:
     * Se utiliza Factory Method porque el sistema debe analizar código en múltiples lenguajes
     * (Java, Python, JavaScript) y cada uno tiene reglas distintas. En lugar de usar condicionales
     * dispersos por el código, el factory centraliza la creación del analizador correcto,
     * facilitando agregar nuevos lenguajes sin modificar la lógica existente (principio Open/Closed).
     */

    /**
     * Justificación del patrón Observer:
     * Se utiliza Observer porque múltiples componentes del sistema pueden necesitar reaccionar
     * cuando el código del estudiante cambia (por ejemplo, actualizar la UI, registrar métricas,
     * o disparar nuevos análisis). El Observer desacopla al emisor del evento de sus receptores,
     * permitiendo agregar o quitar observadores sin modificar CodeAnalysisService.
     */

    // Servicio de IA inyectado para generar explicaciones y sugerencias
    @Autowired
    private AIService aiService;

    /**
     * Cola FIFO para gestionar las solicitudes de análisis pendientes.
     * Se usa Queue (LinkedList) porque los análisis deben procesarse en el orden en que llegan,
     * garantizando equidad y evitando que solicitudes antiguas sean ignoradas (FIFO).
     */
    private Queue<String> analysisQueue = new LinkedList<>();

    /**
     * Mapa para almacenar las funciones detectadas en el código analizado.
     * Se usa HashMap porque permite acceso en O(1) por nombre de función,
     * lo cual es eficiente cuando se necesita consultar o actualizar la descripción
     * de una función específica rápidamente.
     */
    private HashMap<String, String> detectedFunctions = new HashMap<>();

    // Lista de observadores registrados que serán notificados al cambiar el código
    private List<CodeChangeObserver> observers = new ArrayList<>();

    // =========================================================
    // PATRÓN FACTORY METHOD: Interfaz y clases de analizadores
    // =========================================================

    /**
     * Interfaz que define el contrato para todos los analizadores de código.
     * Cada implementación concreta analiza un lenguaje específico.
     */
    public interface CodeAnalyzer {
        /**
         * Analiza el código proporcionado y retorna un resumen de su estructura.
         */
        String analyze(String code);
    }

    /**
     * Analizador concreto para código Java.
     * Detecta clases, métodos y estructuras típicas de Java.
     */
    static class JavaAnalyzer implements CodeAnalyzer {
        /**
         * Analiza código Java buscando clases, métodos y palabras clave del lenguaje.
         */
        @Override
        public String analyze(String code) {
            // Contar elementos estructurales del código Java
            int classCount = countOccurrences(code, "class ");
            int methodCount = countOccurrences(code, "void ") + countOccurrences(code, "public ") + countOccurrences(code, "private ");
            return "Análisis Java: " + classCount + " clase(s) detectada(s), aproximadamente " + methodCount + " declaración(es) de método.";
        }

        // Cuenta cuántas veces aparece una palabra clave en el código
        private int countOccurrences(String text, String keyword) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
            return count;
        }
    }

    /**
     * Analizador concreto para código Python.
     * Detecta funciones, clases e importaciones típicas de Python.
     */
    static class PythonAnalyzer implements CodeAnalyzer {
        /**
         * Analiza código Python buscando definiciones de funciones y clases.
         */
        @Override
        public String analyze(String code) {
            // Contar definiciones de funciones y clases en Python
            int defCount = countOccurrences(code, "def ");
            int classCount = countOccurrences(code, "class ");
            return "Análisis Python: " + defCount + " función(es) detectada(s), " + classCount + " clase(s) detectada(s).";
        }

        // Cuenta cuántas veces aparece una palabra clave en el código
        private int countOccurrences(String text, String keyword) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
            return count;
        }
    }

    /**
     * Analizador concreto para código JavaScript.
     * Detecta funciones, variables y estructuras típicas de JavaScript.
     */
    static class JavaScriptAnalyzer implements CodeAnalyzer {
        /**
         * Analiza código JavaScript buscando funciones, variables y llamadas al DOM.
         */
        @Override
        public String analyze(String code) {
            // Contar funciones y declaraciones de variables en JavaScript
            int functionCount = countOccurrences(code, "function ") + countOccurrences(code, "=> ");
            int varCount = countOccurrences(code, "const ") + countOccurrences(code, "let ") + countOccurrences(code, "var ");
            return "Análisis JavaScript: " + functionCount + " función(es) detectada(s), " + varCount + " declaración(es) de variable.";
        }

        // Cuenta cuántas veces aparece una palabra clave en el código
        private int countOccurrences(String text, String keyword) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
            return count;
        }
    }

    /**
     * Factory Method: retorna el analizador correcto según el lenguaje de programación.
     * Centraliza la lógica de creación y permite extender fácilmente con nuevos lenguajes.
     */
    public CodeAnalyzer createAnalyzer(String language) {
        // Seleccionar el analizador adecuado según el lenguaje recibido
        return switch (language.toLowerCase()) {
            case "java" -> new JavaAnalyzer();
            case "python" -> new PythonAnalyzer();
            case "javascript" -> new JavaScriptAnalyzer();
            default -> code -> "Analizador genérico: lenguaje '" + language + "' no tiene analizador especializado.";
        };
    }

    // =========================================================
    // PATRÓN OBSERVER: Gestión de observadores
    // =========================================================

    /**
     * Registra un nuevo observador que será notificado cuando el código cambie.
     */
    public void addObserver(CodeChangeObserver observer) {
        observers.add(observer);
    }

    /**
     * Elimina un observador de la lista para que deje de recibir notificaciones.
     */
    public void removeObserver(CodeChangeObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores registrados que el código ha cambiado.
     * Itera sobre la lista y llama al método onCodeChanged de cada uno.
     */
    public void notifyObservers(String code, String language) {
        // Recorrer todos los observadores y notificarles el cambio de código
        for (CodeChangeObserver observer : observers) {
            observer.onCodeChanged(code, language);
        }
    }

    /**
     * Implementación del método del Observer: reacciona cuando el código cambia.
     * En este caso, registra el evento de cambio para trazabilidad.
     */
    @Override
    public void onCodeChanged(String newCode, String language) {
        // Registrar que se detectó un cambio en el código del estudiante
        System.out.println("CodeAnalysisService notificado: el código en " + language + " ha cambiado.");
    }

    // =========================================================
    // MÉTODOS PRINCIPALES DEL SERVICIO
    // =========================================================

    /**
     * Analiza el código del estudiante: lo encola, selecciona el analizador correcto,
     * obtiene explicación y sugerencia de la IA, notifica observadores y retorna el resultado.
     */
    public AnalysisResult analyzeCode(String code, String language) {
        // Agregar el código a la cola de análisis pendientes
        analysisQueue.add(code);

        // Obtener el siguiente código a procesar de la cola (FIFO)
        String codeToAnalyze = analysisQueue.poll();

        // Crear el analizador adecuado para el lenguaje usando el Factory Method
        CodeAnalyzer analyzer = createAnalyzer(language);
        String structureAnalysis = analyzer.analyze(codeToAnalyze);

        // Almacenar el análisis estructural como una función detectada en el mapa
        detectedFunctions.put("análisis_" + language, structureAnalysis);

        // Solicitar a la IA una explicación del código para el estudiante
        String explanation = aiService.explainCode(codeToAnalyze, language);

        // Solicitar a la IA una sugerencia del siguiente paso a seguir
        String suggestion = aiService.suggestNextStep(codeToAnalyze, language);

        // Notificar a todos los observadores que el código fue analizado
        notifyObservers(codeToAnalyze, language);

        // Retornar el resultado completo del análisis
        return new AnalysisResult(explanation, suggestion, language);
    }

    /**
     * Detecta el lenguaje de programación del código basándose en palabras clave características.
     * Retorna el nombre del lenguaje detectado o "Unknown" si no se reconoce.
     */
    public String detectLanguage(String code) {
        // Verificar palabras clave típicas de Python
        if (code.contains("def ") || (code.contains("import ") && !code.contains(";"))) {
            return "Python";
        }

        // Verificar palabras clave típicas de Java
        if (code.contains("public class") || code.contains("System.out")) {
            return "Java";
        }

        // Verificar palabras clave típicas de JavaScript
        if (code.contains("function") || code.contains("console.log")) {
            return "JavaScript";
        }

        // No se pudo determinar el lenguaje con las palabras clave disponibles
        return "Unknown";
    }

    // =========================================================
    // CLASE INTERNA: AnalysisResult
    // =========================================================

    /**
     * Clase que encapsula el resultado de un análisis de código.
     * Contiene la explicación generada por la IA, la sugerencia del siguiente paso y el lenguaje detectado.
     */
    public static class AnalysisResult {

        // Explicación del código generada por la IA
        private final String explanation;

        // Sugerencia del siguiente paso para el estudiante
        private final String suggestion;

        // Lenguaje de programación del código analizado
        private final String language;

        /**
         * Constructor que inicializa todos los campos del resultado de análisis.
         */
        public AnalysisResult(String explanation, String suggestion, String language) {
            this.explanation = explanation;
            this.suggestion = suggestion;
            this.language = language;
        }

        /**
         * Retorna la explicación del código generada por la IA.
         */
        public String getExplanation() {
            return explanation;
        }

        /**
         * Retorna la sugerencia del siguiente paso para el estudiante.
         */
        public String getSuggestion() {
            return suggestion;
        }

        /**
         * Retorna el lenguaje de programación del código analizado.
         */
        public String getLanguage() {
            return language;
        }
    }
}
