package com.codeTutor.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codeTutor.backend.dto.request.RunCodeRequest;
import com.codeTutor.backend.dto.response.RunCodeResponse;

/**
 * Executes student code using Judge0 CE API via RapidAPI.
 * Free tier: 50 submissions/day.
 * Supports Java, Python, JavaScript and TypeScript.
 */
@Service
public class CodeExecutionService {

    // Judge0 language IDs
    private static final Map<String, Integer> LANGUAGE_IDS = Map.of(
            "java", 62,
            "python", 71,
            "javascript", 63,
            "typescript", 74,
            "cpp", 54
    );

    @Value("${judge0.api.url:https://judge0-ce.p.rapidapi.com}")
    private String apiUrl;

    @Value("${judge0.api.key:${JUDGE0_API_KEY:}}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Submits code to Judge0 and returns the execution result.
     */
    public RunCodeResponse execute(RunCodeRequest request) {
        Integer languageId = LANGUAGE_IDS.get(request.getLanguage().toLowerCase());
        if (languageId == null) {
            return RunCodeResponse.builder()
                    .stderr("Lenguaje no soportado: " + request.getLanguage())
                    .exitCode(1)
                    .build();
        }

        if (apiKey == null || apiKey.isBlank()) {
            return RunCodeResponse.builder()
                    .stderr("API de ejecución no configurada. Configura JUDGE0_API_KEY en las variables de entorno.")
                    .exitCode(1)
                    .build();
        }

        try {
            // Base64 encode the source code
            String encodedCode = java.util.Base64.getEncoder().encodeToString(
                    request.getCode().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String encodedStdin = "";
            if (request.getStdin() != null && !request.getStdin().isBlank()) {
                encodedStdin = java.util.Base64.getEncoder().encodeToString(
                        request.getStdin().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            // Build request body
            String body = "{"
                    + "\"language_id\": " + languageId + ","
                    + "\"source_code\": \"" + encodedCode + "\","
                    + "\"stdin\": \"" + encodedStdin + "\""
                    + "}";

            // Submit and wait for result (synchronous mode)
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/submissions?base64_encoded=true&wait=true"))
                    .header("Content-Type", "application/json")
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com")
                    .POST(HttpRequest.BodyPublishers.ofString(body, java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                return RunCodeResponse.builder()
                        .stderr("API key inválida o límite de uso alcanzado.")
                        .exitCode(1)
                        .build();
            }

            if (response.statusCode() == 429) {
                return RunCodeResponse.builder()
                        .stderr("Límite diario de ejecuciones alcanzado. Intenta mañana.")
                        .exitCode(1)
                        .build();
            }

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                return RunCodeResponse.builder()
                        .stderr("Error del servidor de ejecución: " + response.statusCode())
                        .exitCode(1)
                        .build();
            }

            return parseJudge0Response(response.body());

        } catch (Exception e) {
            return RunCodeResponse.builder()
                    .stderr("Error al ejecutar el código: " + e.getMessage())
                    .exitCode(1)
                    .build();
        }
    }

    /**
     * Parses Judge0 JSON response.
     * Judge0 returns: { stdout, stderr, status: { id, description }, compile_output }
     */
    private RunCodeResponse parseJudge0Response(String json) {
        String stdout = decodeBase64Field(json, "stdout");
        String stderr = decodeBase64Field(json, "stderr");
        String compileOutput = decodeBase64Field(json, "compile_output");

        // If there's a compile error, show it as stderr
        if ((stderr == null || stderr.isBlank()) && compileOutput != null && !compileOutput.isBlank()) {
            stderr = compileOutput;
        }

        // Extract status id to determine exit code
        int exitCode = 0;
        int statusId = extractStatusId(json);
        if (statusId != 3) { // 3 = Accepted (success)
            exitCode = 1;
        }

        return RunCodeResponse.builder()
                .stdout(stdout != null ? stdout : "")
                .stderr(stderr != null ? stderr : "")
                .exitCode(exitCode)
                .build();
    }

    private String decodeBase64Field(String json, String field) {
        try {
            String key = "\"" + field + "\":";
            int idx = json.indexOf(key);
            if (idx == -1) return null;

            int valueStart = idx + key.length();
            // Skip whitespace
            while (valueStart < json.length() && json.charAt(valueStart) == ' ') valueStart++;

            if (json.charAt(valueStart) == 'n') return null; // null value

            // It's a string value
            int start = json.indexOf("\"", valueStart) + 1;
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }

            String base64Value = json.substring(start, end);
            if (base64Value.isEmpty()) return "";

            // Remove escaped newlines that Judge0 might include
            base64Value = base64Value.replace("\\n", "\n").replace("\n", "");

            return new String(java.util.Base64.getDecoder().decode(base64Value),
                    java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private int extractStatusId(String json) {
        try {
            int statusIdx = json.indexOf("\"status\"");
            if (statusIdx == -1) return 0;
            String idKey = "\"id\":";
            int idIdx = json.indexOf(idKey, statusIdx);
            if (idIdx == -1) return 0;
            int start = idIdx + idKey.length();
            while (start < json.length() && json.charAt(start) == ' ') start++;
            int end = start;
            while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) {
            return 0;
        }
    }
}
