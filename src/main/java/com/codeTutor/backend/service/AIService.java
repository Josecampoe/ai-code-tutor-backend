package com.codeTutor.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Servicio principal de inteligencia artificial.
 * Implementa el patrón Singleton gestionado por Spring.
 * Se encarga de toda la comunicación con la API de Gemini.
 */
@Service
public class AIService {

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" +
            "gemini-1.5-flash:generateContent";

    // La key se inyecta automáticamente desde application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    // Cliente HTTP reutilizable — parte del patrón Singleton
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Explica el código del estudiante de forma clara para principiantes.
     */
    public String explainCode(String code, String language) {
        String prompt = "Eres un tutor de programación para principiantes. " +
                "Explica de forma simple qué hace este código en " + language +
                ". Usa lenguaje sencillo. Código: " + code;
        return callGeminiApi(prompt);
    }

    /**
     * Sugiere el siguiente paso para continuar el desarrollo.
     */
    public String suggestNextStep(String code, String language) {
        String prompt = "Eres un tutor de programación. El estudiante aprende " +
                language + " y tiene este código: " + code +
                ". Sugiere el siguiente paso lógico. NO escribas el código completo.";
        return callGeminiApi(prompt);
    }

    /**
     * Genera una guía de pasos iniciales para el proyecto descrito.
     */
    public String generateProjectGuide(String projectDescription) {
        String prompt = "Eres un tutor de programación para principiantes. " +
                "El estudiante quiere desarrollar: " + projectDescription +
                ". Genera una guía con pasos simples para que aprenda. " +
                "NO escribas el código completo del proyecto.";
        return callGeminiApi(prompt);
    }

    /**
     * Realiza la llamada HTTP real a la API de Gemini.
     */
    private String callGeminiApi(String prompt) {
        try {
            String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \""
                    + prompt.replace("\"", "\\\"").replace("\n", "\\n")
                    + "\"}]}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return extractTextFromResponse(response.body());

        } catch (Exception e) {
            return "Error al conectar con la IA: " + e.getMessage();
        }
    }

    /**
     * Extrae el texto generado por Gemini del JSON de respuesta.
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            int textIndex = jsonResponse.indexOf("\"text\":");
            if (textIndex == -1) return "No se pudo obtener respuesta de la IA";
            int start = jsonResponse.indexOf("\"", textIndex + 7) + 1;
            int end = jsonResponse.indexOf("\"", start);
            return jsonResponse.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
        } catch (Exception e) {
            return "Error al procesar la respuesta de la IA";
        }
    }
}
