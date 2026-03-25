package com.codeTutor.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Servicio de análisis de código que detecta el lenguaje, analiza la estructura del código
 * y coordina explicaciones y sugerencias mediante la IA.
 *
 * Implementa tres patrones de diseño:
 * - Strategy: para seleccionar el algoritmo de análisis según el lenguaje en tiempo de ejecución.
 * - Factory Method: para registrar y obtener la estrategia correcta por lenguaje.
 * - Observer: para notificar automáticamente cuando el código cambia.
 */
@Service
public class CodeAnalysisService implements CodeChangeObserver {

    /**
     * Justificación del patrón Strategy:
     * Se utiliza Strategy porque cada lenguaje de programación requiere un algoritmo de análisis
     * diferente. En lugar de un switch/if-else que crece con cada nuevo lenguaje, cada algoritmo
     * se encapsula en una clase separada que implementa la misma interfaz CodeAnalyzer.
     * Esto permite agregar nuevos lenguajes registrando una nueva estrategia en el mapa,
     * sin modificar ningún código existente (principio Open/Closed).
     * La estrategia se selecciona en tiempo de ejecución según el lenguaje recibido.
     */

    /**
     * Justificación del patrón Observer:
     * Se utiliza Observer porque múltiples componentes pueden necesitar reaccionar
     * cuando el código del estudiante cambia. El Observer desacopla al emisor del evento
     * de sus receptores, permitiendo agregar o quitar observadores sin modificar este servicio.
     */

    // Servicio de IA inyectado para generar explicaciones y sugerencias
    @Autowired
    private AIService aiService;

    /**
     * Cola FIFO para gestionar las solicitudes de análisis pendientes.
     * Se usa Queue porque los análisis deben procesarse en el orden en que llegan (FIFO),
     * garantizando equidad entre solicitudes.
     */
    private final Queue<String> analysisQueue = new LinkedList<>();

    /**
     * Mapa para almacenar las funciones detectadas en el código analizado.
     * Se usa HashMap por su acceso O(1) por nombre de función.
     */
    private final HashMap<String, String> detectedFunctions = new HashMap<>();

    // Lista de observadores registrados
    private final List<CodeChangeObserver> observers = new ArrayList<>();

    /**
     * Registro de estrategias disponibles por lenguaje.
     * Se usa HashMap para que agregar un nuevo lenguaje sea solo registrar
     * una nueva entrada sin tocar ningún otro código.
     */
    private final Map<String, CodeAnalyzer> analyzerStrategies = new HashMap<>();

    // =========================================================
    // PATRÓN STRATEGY: Interfaz y estrategias concretas
    // =========================================================

    /**
     * Interfaz Strategy que define el contrato para todos los algoritmos de análisis.
     * Cada implementación encapsula la lógica de análisis de un lenguaje específico.
     */
    public interface CodeAnalyzer {
        /**
         * Analiza el código y retorna un resumen de su estructura.
         */
        String analyze(String code);
    }

    /**
     * Estrategia concreta para Java.
     * Detecta clases, métodos y estructuras típicas de Java.
     */
    static class JavaAnalyzer implements CodeAnalyzer {
        @Override
        public String analyze(String code) {
            // Contar elementos estructurales del código Java
            int classCount = countOccurrences(code, "class ");
            int methodCount = countOccurrences(code, "void ")
                    + countOccurrences(code, "public ")
                    + countOccurrences(code, "private ");
            return "Análisis Java: " + classCount + " clase(s), " + methodCount + " declaración(es) de método.";
        }

        private int countOccurrences(String text, String keyword) {
            int count = 0, index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) { count++; index += keyword.length(); }
            return count;
        }
    }

    /**
     * Estrategia concreta para Python.
     * Detecta funciones, clases e importaciones típicas de Python.
     */
    static class PythonAnalyzer implements CodeAnalyzer {
        @Override
        public String analyze(String code) {
            // Contar definiciones de funciones y clases en Python
            int defCount = countOccurrences(code, "def ");
            int classCount = countOccurrences(code, "class ");
            return "Análisis Python: " + defCount + " función(es), " + classCount + " clase(s).";
        }

        private int countOccurrences(String text, String keyword) {
            int count = 0, index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) { count++; index += keyword.length(); }
            return count;
        }
    }

    /**
     * Estrategia concreta para JavaScript.
     * Detecta funciones, variables y estructuras típicas de JavaScript.
     */
    static class JavaScriptAnalyzer implements CodeAnalyzer {
        @Override
        public String analyze(String code) {
            // Contar funciones y declaraciones de variables en JavaScript
            int functionCount = countOccurrences(code, "function ") + countOccurrences(code, "=> ");
            int varCount = countOccurrences(code, "const ") + countOccurrences(code, "let ") + countOccurrences(code, "var ");
            return "Análisis JavaScript: " + functionCount + " función(es), " + varCount + " variable(s).";
        }

        private int countOccurrences(String text, String keyword) {
            int count = 0, index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) { count++; index += keyword.length(); }
            return count;
        }
    }

    /**
     * Estrategia concreta para TypeScript.
     * Detecta interfaces, tipos y estructuras típicas de TypeScript.
     * Ejemplo de cómo agregar un nuevo lenguaje sin modificar código existente.
     */
    static class TypeScriptAnalyzer implements CodeAnalyzer {
        @Override
        public String analyze(String code) {
            // Contar interfaces, tipos y funciones en TypeScript
            int interfaceCount = countOccurrences(code, "interface ");
            int typeCount = countOccurrences(code, "type ");
            int functionCount = countOccurrences(code, "function ") + countOccurrences(code, "=> ");
            return "Análisis TypeScript: " + interfaceCount + " interfaz(ces), "
                    + typeCount + " tipo(s), " + functionCount + " función(es).";
        }

        private int countOccurrences(String text, String keyword) {
            int count = 0, index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) { count++; index += keyword.length(); }
            return count;
        }
    }

    /**
     * Inicializa el registro de estrategias disponibles.
     * Para agregar un nuevo lenguaje solo se añade una línea aquí.
     */
    @Autowired
    public void initStrategies() {
        // Registrar cada estrategia con su clave de lenguaje
        analyzerStrategies.put("java", new JavaAnalyzer());
        analyzerStrategies.put("python", new PythonAnalyzer());
        analyzerStrategies.put("javascript", new JavaScriptAnalyzer());
        analyzerStrategies.put("typescript", new TypeScriptAnalyzer());
    }

    /**
     * Selecciona y retorna la estrategia de análisis correcta para el lenguaje dado.
     * Si el lenguaje no tiene estrategia registrada, retorna una genérica.
     */
    public CodeAnalyzer createAnalyzer(String language) {
        // Buscar la estrategia registrada para el lenguaje (en minúsculas)
        return analyzerStrategies.getOrDefault(
                language.toLowerCase(),
                code -> "Análisis genérico: lenguaje '" + language + "' no tiene estrategia registrada."
        );
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
     * Elimina un observador de la lista.
     */
    public void removeObserver(CodeChangeObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores que el código ha cambiado.
     */
    public void notifyObservers(String code, String language) {
        for (CodeChangeObserver observer : observers) {
            observer.onCodeChanged(code, language);
        }
    }

    /**
     * Reacciona cuando el código cambia — implementación del Observer.
     */
    @Override
    public void onCodeChanged(String newCode, String language) {
        System.out.println("CodeAnalysisService notificado: código en " + language + " ha cambiado.");
    }

    // =========================================================
    // MÉTODOS PRINCIPALES
    // =========================================================

    /**
     * Analiza el código: lo encola, aplica la estrategia correcta,
     * obtiene explicación y sugerencia de la IA, notifica observadores y retorna el resultado.
     */
    public AnalysisResult analyzeCode(String code, String language) {
        // Encolar la solicitud de análisis
        analysisQueue.add(code);
        String codeToAnalyze = analysisQueue.poll();

        // Seleccionar y aplicar la estrategia de análisis para el lenguaje
        CodeAnalyzer strategy = createAnalyzer(language);
        String structureAnalysis = strategy.analyze(codeToAnalyze);

        // Almacenar el resultado estructural en el mapa de funciones detectadas
        detectedFunctions.put("análisis_" + language, structureAnalysis);

        // Obtener explicación y sugerencia de la IA
        String explanation = aiService.explainCode(codeToAnalyze, language);
        String suggestion = aiService.suggestNextStep(codeToAnalyze, language);

        // Notificar a los observadores
        notifyObservers(codeToAnalyze, language);

        return new AnalysisResult(explanation, suggestion, language);
    }

    /**
     * Detecta el lenguaje de programación basándose en palabras clave del código.
     */
    public String detectLanguage(String code) {
        if (code.contains("def ") || (code.contains("import ") && !code.contains(";"))) return "Python";
        if (code.contains("public class") || code.contains("System.out")) return "Java";
        if (code.contains("interface ") && code.contains(":")) return "TypeScript";
        if (code.contains("function") || code.contains("console.log")) return "JavaScript";
        return "Unknown";
    }

    // =========================================================
    // CLASE INTERNA: AnalysisResult
    // =========================================================

    /**
     * Encapsula el resultado completo de un análisis de código.
     */
    public static class AnalysisResult {

        private final String explanation;
        private final String suggestion;
        private final String language;

        public AnalysisResult(String explanation, String suggestion, String language) {
            this.explanation = explanation;
            this.suggestion = suggestion;
            this.language = language;
        }

        public String getExplanation() { return explanation; }
        public String getSuggestion() { return suggestion; }
        public String getLanguage() { return language; }
    }
}
