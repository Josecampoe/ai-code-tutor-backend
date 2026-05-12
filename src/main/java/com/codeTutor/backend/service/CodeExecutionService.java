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
 * Executes student code using Piston API (free, no API key required).
 * Supports Java, Python, JavaScript and TypeScript.
 * Piston docs: https://github.com/engineer-man/piston
 */
@Service
public class CodeExecutionService {

    // Piston language identifiers and versions
    private static final Map<String, String[]> LANGUAGE_CONFIG = Map.of(
            "java",       new String[]{"java", "15.0.2"},
            "python",     new String[]{"python", "3.10.0"},
            "javascript", new String[]{"javascript", "18.15.0"},
            "typescript", new String[]{"typescript", "5.0.3"}
    );

    @Value("${piston.api.url}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Submits code to Piston and returns the execution result.
     */
    public RunCodeResponse execute(RunCodeRequest request) {
        String[] config = LANGUAGE_CONFIG.get(request.getLanguage().toLowerCase());
        if (config == null) {
            return RunCodeResponse.builder()
                    .stderr("Lenguaje no soportado: " + request.getLanguage())
                    .exitCode(1)
                    .build();
        }

        try {
            String language = config[0];
            String version = config[1];
            String stdin = request.getStdin() != null ? request.getStdin() : "";

            // Escape code for JSON embedding
            String safeCode = request.getCode()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\r\n", "\\n")
                    .replace("\r", "\\n")
                    .replace("\n", "\\n")
                    .replace("\t", "\\t");

            String safeStdin = stdin
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String body = "{"
                    + "\"language\": \"" + language + "\","
                    + "\"version\": \"" + version + "\","
                    + "\"files\": [{\"content\": \"" + safeCode + "\"}],"
                    + "\"stdin\": \"" + safeStdin + "\""
                    + "}";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/execute"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body,
                            java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                return RunCodeResponse.builder()
                        .stderr("La API de ejecución de código requiere autorización. Configura PISTON_API_URL con una instancia propia.")
                        .exitCode(1)
                        .build();
            }

            if (response.statusCode() != 200) {
                return RunCodeResponse.builder()
                        .stderr("Error del servidor de ejecución: " + response.statusCode())
                        .exitCode(1)
                        .build();
            }

            return parseResponse(response.body());

        } catch (Exception e) {
            return RunCodeResponse.builder()
                    .stderr("Error al ejecutar el código: " + e.getMessage())
                    .exitCode(1)
                    .build();
        }
    }

    /**
     * Parses Piston JSON response.
     * Piston returns: { run: { stdout, stderr, code, signal } }
     */
    private RunCodeResponse parseResponse(String json) {
        String stdout = extractNestedField(json, "run", "stdout");
        String stderr = extractNestedField(json, "run", "stderr");
        Integer exitCode = extractNestedInt(json, "run", "code");

        return RunCodeResponse.builder()
                .stdout(stdout != null ? stdout : "")
                .stderr(stderr != null ? stderr : "")
                .exitCode(exitCode != null ? exitCode : 0)
                .build();
    }

    private String extractNestedField(String json, String parent, String field) {
        try {
            int parentIdx = json.indexOf("\"" + parent + "\"");
            if (parentIdx == -1) return null;
            String key = "\"" + field + "\": \"";
            int start = json.indexOf(key, parentIdx);
            if (start == -1) return null;
            start += key.length();
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }
            return json.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"");
        } catch (Exception e) {
            return null;
        }
    }

    private Integer extractNestedInt(String json, String parent, String field) {
        try {
            int parentIdx = json.indexOf("\"" + parent + "\"");
            if (parentIdx == -1) return null;
            String key = "\"" + field + "\": ";
            int start = json.indexOf(key, parentIdx);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            String val = json.substring(start, end).trim();
            return val.equals("null") ? null : Integer.parseInt(val);
        } catch (Exception e) {
            return null;
        }
    }
}
