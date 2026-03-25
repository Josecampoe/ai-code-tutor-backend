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
 * Se encarga de toda la comunicación con la API de Groq.
 */
@Service
public class AIService {

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
    public String explainCode(String code, String language) {
        String prompt = "Eres un tutor de programación para principiantes. " +
                "Explica de forma simple qué hace este código en " + language +
                ". Usa lenguaje sencillo. Código: " + code;
        return callGroqApi(prompt);
    }

    /**
     * Sugiere el siguiente paso para continuar el desarrollo.
     */
    public String suggestNextStep(String code, String language) {
        String prompt = "Eres un tutor de programación. El estudiante aprende " +
                language + " y tiene este código: " + code +
                ". Sugiere el siguiente paso lógico. NO escribas el código completo.";
        return callGroqApi(prompt);
    }

    /**
     * Genera una guía de pasos iniciales para el proyecto descrito.
     */
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
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
