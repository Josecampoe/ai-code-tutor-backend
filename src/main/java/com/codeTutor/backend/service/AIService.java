package com.codeTutor.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio principal de inteligencia artificial.
 * Implementa el patrón Singleton gestionado por Spring.
 * Se encarga de toda la comunicación con la API de Groq.
 */
@Service
public class AIService implements AIServiceInterface {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    // La key se inyecta desde variable de entorno GROQ_API_KEY
    @Value("${groq.api.key}")
    private String apiKey;

    // Cliente HTTP reutilizable — parte del patrón Singleton
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Explica el código del estudiante de forma clara para principiantes.
     */
    @Override
    public String explainCode(String code, String language) {
        String prompt = "Eres un tutor de programación para principiantes. " +
                "Explica de forma simple qué hace este código en " + language +
                ". Usa lenguaje sencillo. Código: " + code;
        return callGroqApi(prompt);
    }

    /**
     * Sugiere el siguiente paso para continuar el desarrollo.
     */
    @Override
    public String suggestNextStep(String code, String language) {
        String prompt = "Eres un tutor de programación. El estudiante aprende " +
                language + " y tiene este código: " + code +
                ". Sugiere el siguiente paso lógico. NO escribas el código completo.";
        return callGroqApi(prompt);
    }

    // =========================================================
    // MÉTODOS DEL MÓDULO "APRENDE CON IA"
    // =========================================================

    /**
     * Genera el enunciado de un ejercicio práctico sobre un tema específico.
     * El ejercicio debe ser claro, concreto y apropiado para principiantes.
     */
    @Override
    public String generateExerciseStatement(String topicName, String category, String language) {
        String prompt = "Eres un tutor de programación. Genera un ejercicio práctico sobre el tema '"
                + topicName + "' (categoría: " + category + ") en el lenguaje " + language + ". "
                + "El ejercicio debe ser claro, concreto y apropiado para principiantes. "
                + "Incluye: qué debe implementar el estudiante, qué entradas y salidas se esperan. "
                + "NO incluyas la solución. Solo el enunciado del ejercicio.";
        return callGroqApi(prompt);
    }

    /**
     * Genera un código de inicio (esqueleto) para que el estudiante complete el ejercicio.
     * El esqueleto incluye la estructura básica pero deja los detalles para que el estudiante los implemente.
     */
    @Override
    public String generateStarterCode(String topicName, String language) {
        String prompt = "Eres un tutor de programación. Genera un código esqueleto en " + language
                + " para un ejercicio sobre '" + topicName + "'. "
                + "El esqueleto debe incluir: la estructura de la clase/función, comentarios indicando qué debe implementar el estudiante, "
                + "y los métodos vacíos o con TODO. NO implementes la lógica, solo la estructura.";
        return callGroqApi(prompt);
    }

    /**
     * Genera una pista para el ejercicio sin revelar la solución completa.
     * La pista debe orientar al estudiante sin darle la respuesta directa.
     */
    @Override
    public String generateHint(String exerciseStatement, String language) {
        String prompt = "Eres un tutor de programación. El estudiante está resolviendo este ejercicio en "
                + language + ": " + exerciseStatement
                + ". Da una pista útil que lo oriente sin revelar la solución completa. "
                + "La pista debe ser breve y motivadora.";
        return callGroqApi(prompt);
    }

    /**
     * Evalúa la solución del estudiante y retorna retroalimentación detallada.
     * Indica si es correcta, qué está bien y qué puede mejorar.
     */
    @Override
    public String evaluateSolution(String exerciseStatement, String solutionCode, String language) {
        String prompt = "Eres un tutor de programación. Evalúa la siguiente solución en " + language
                + " para este ejercicio: " + exerciseStatement
                + ". Solución del estudiante: " + solutionCode
                + ". Indica si la solución es correcta o no. "
                + "Explica qué está bien, qué puede mejorar y por qué. Usa lenguaje amigable para principiantes. "
                + "Si es correcta, empieza con 'Correcto' o 'Bien hecho'.";
        return callGroqApi(prompt);
    }

    /**
     * Responde al mensaje del estudiante en el chat conversacional.
     * Usa el historial y el código actual como contexto para dar respuestas coherentes.
     */
    @Override
    public String chat(String message, String conversationHistory, String currentCode, String language) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres CodeTutor, un tutor de programación amigable y motivador para estudiantes. ");
        prompt.append("Tu personalidad: cercano, paciente, entusiasta con la programación. ");
        prompt.append("Cuando el estudiante te saluda, responde presentándote y pregunta en qué puedes ayudarle hoy. ");
        prompt.append("Cuando haga preguntas de programación, explica de forma clara y simple. ");
        prompt.append("Cuando pida ayuda con código, analiza el código del editor si está disponible. ");
        prompt.append("Responde siempre en el mismo idioma que el estudiante. ");
        prompt.append("Sé conciso pero completo. Usa emojis ocasionalmente para ser más amigable.\n\n");

        // Incluir historial de conversación para mantener contexto
        if (conversationHistory != null && !conversationHistory.isBlank()) {
            prompt.append("Historial de la conversación:\n").append(conversationHistory).append("\n\n");
        }

        // Incluir código del editor si está disponible
        if (currentCode != null && !currentCode.isBlank()) {
            prompt.append("Código actual del estudiante en el editor (").append(language).append("):\n");
            prompt.append(currentCode).append("\n\n");
        }

        prompt.append("Mensaje del estudiante: ").append(message);

        return callGroqApi(prompt.toString());
    }

    /**
     * Genera una guía de pasos iniciales para el proyecto descrito.
     */
    @Override
    public String generateProjectGuide(String projectDescription) {
        String prompt = "Eres un tutor de programación para principiantes. " +
                "El estudiante quiere desarrollar: " + projectDescription +
                ". Genera una guía con pasos simples para que aprenda. " +
                "NO escribas el código completo del proyecto.";
        return callGroqApi(prompt);
    }

    /**
     * Realiza la llamada HTTP a la API de Groq (compatible con OpenAI).
     */
    private String callGroqApi(String prompt) {
        try {
            // Construir el body en formato OpenAI chat completions
            String requestBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \""
                    + prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
                    + "\"}],"
                    + "\"max_tokens\": 1024"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                return "Error Groq [" + response.statusCode() + "]: " + response.body();
            }

            return extractTextFromResponse(response.body());

        } catch (Exception e) {
            return "Error al conectar con la IA: " + e.getMessage();
        }
    }

    /**
     * Extrae el texto de la respuesta JSON de Groq (formato OpenAI).
     * Estructura: choices[0].message.content
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            // Buscar "content": dentro de choices → message
            int choicesIndex = jsonResponse.indexOf("\"choices\"");
            if (choicesIndex == -1) return "Respuesta inesperada de la IA: " + jsonResponse;

            int contentIndex = jsonResponse.indexOf("\"content\":", choicesIndex);
            if (contentIndex == -1) return "No se encontró contenido en la respuesta";

            int start = jsonResponse.indexOf("\"", contentIndex + 10) + 1;

            // Buscar cierre correcto ignorando comillas escapadas
            int end = start;
            while (end < jsonResponse.length()) {
                end = jsonResponse.indexOf("\"", end);
                if (end == -1) break;
                int backslashes = 0;
                int check = end - 1;
                while (check >= 0 && jsonResponse.charAt(check) == '\\') {
                    backslashes++;
                    check--;
                }
                if (backslashes % 2 == 0) break;
                end++;
            }

            if (end == -1 || end <= start) return "Error al parsear respuesta de la IA";

            return jsonResponse.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            return "Error al procesar la respuesta de la IA: " + e.getMessage();
        }
    }
}
