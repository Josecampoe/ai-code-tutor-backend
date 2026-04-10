package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.RunCodeRequest;
import com.codeTutor.backend.dto.response.RunCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;

/**
 * Executes student code using Judge0 API.
 * Supports Java, Python, JavaScript and TypeScript.
 */
@Service
public class CodeExecutionService {

    // Language IDs for Judge0 CE
    private static final Map<String, Integer> LANGUAGE_IDS = Map.of(
            "java",       62,
            "python",     71,
            "javascript", 63,
            "typescript", 74,
            "c",          50,
            "cpp",        54
    );

    @Value("${judge0.api.url}")
    private String apiUrl;

    @Value("${judge0.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Submits code to Judge0 and waits for the result.
     * Returns stdout, stderr and exit code.
     */
    public RunCodeResponse execute(RunCodeRequest request) {
        Integer languageId = LANGUAGE_IDS.get(request.getLanguage().toLowerCase());
        if (languageId == null) {
            return RunCodeResponse.builder()
                    .stderr("Lenguaje no soportado: " + request.getLanguage())
                    .exitCode(1)
                    .build();
        }

        try {
            // Encode code and stdin in base64 as Judge0 requires
            String encodedCode = Base64.getEncoder().encodeToString(
                    request.getCode().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String encodedStdin = request.getStdin() != null
                    ? Base64.getEncoder().encodeToString(
                            request.getStdin().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                    : "";

            String body = "{"
                    + "\"language_id\": " + languageId + ","
                    + "\"source_code\": \"" + encodedCode + "\","
                    + "\"stdin\": \"" + encodedStdin + "\","
                    + "\"base64_encoded\": true,"
                    + "\"wait\": true"
                    + "}";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/submissions?base64_encoded=true&wait=true"))
                    .header("Content-Type", "application/json")
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            return parseResponse(response.body());

        } catch (Exception e) {
            return RunCodeResponse.builder()
                    .stderr("Error al ejecutar el código: " + e.getMessage())
                    .exitCode(1)
                    .build();
        }
    }

    /**
     * Parses Judge0 JSON response and decodes base64 output fields.
     */
    private RunCodeResponse parseResponse(String json) {
        String stdout = decodeBase64Field(json, "stdout");
        String stderr = decodeBase64Field(json, "stderr");
        String compileOutput = decodeBase64Field(json, "compile_output");
        String message = extractField(json, "message");
        Integer exitCode = extractExitCode(json);
        Double time = extractTime(json);

        // Combine compile errors with stderr
        String errorOutput = "";
        if (compileOutput != null && !compileOutput.isBlank()) errorOutput += compileOutput;
        if (stderr != null && !stderr.isBlank()) errorOutput += stderr;

        return RunCodeResponse.builder()
                .stdout(stdout != null ? stdout : "")
                .stderr(errorOutput)
                .message(message)
                .exitCode(exitCode)
                .time(time)
                .build();
    }

    private String decodeBase64Field(String json, String field) {
        try {
            String key = "\"" + field + "\": \"";
            int start = json.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf("\"", start);
            if (end == -1) return null;
            String encoded = json.substring(start, end);
            if (encoded.equals("null")) return null;
            return new String(Base64.getDecoder().decode(encoded),
                    java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractField(String json, String field) {
        try {
            String key = "\"" + field + "\": \"";
            int start = json.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf("\"", start);
            return end == -1 ? null : json.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer extractExitCode(String json) {
        try {
            String key = "\"exit_code\": ";
            int start = json.indexOf(key);
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

    private Double extractTime(String json) {
        try {
            String key = "\"time\": \"";
            int start = json.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = json.indexOf("\"", start);
            return end == -1 ? null : Double.parseDouble(json.substring(start, end));
        } catch (Exception e) {
            return null;
        }
    }
}
