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

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    // API key injected from environment variable GROQ_API_KEY
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
     * Responds to student messages using a strict tutor system prompt.
     * Restricts responses to programming topics only and enforces short, guiding answers.
     */
    @Override
    public String chat(String message, String conversationHistory, String currentCode, String language) {
        StringBuilder prompt = new StringBuilder();

        boolean isExerciseHelp = message.contains("Necesito ayuda con este ejercicio:");

        // System prompt — defines the AI's role and behavior rules
        if (isExerciseHelp) {
            prompt.append("Eres un tutor de programación integrado en CodeLearn. ")
                  .append("Tu rol es GUIAR, nunca resolver. Da una pista corta (máximo 3 oraciones). ")
                  .append("Nunca escribas la solución completa. Haz una pregunta al final para guiar al estudiante. ")
                  .append("Si el código está vacío, explica la estructura general sin escribir el código. ")
                  .append("Responde en español. Sin introducciones largas. Ve directo al punto.\n\n");
        } else {
            prompt.append("Eres el tutor de programación de CodeLearn, un editor educativo. ")
                  .append("SOLO respondes preguntas sobre programación, código, estructuras de datos, errores y desarrollo de software. ")
                  .append("Si el estudiante pregunta algo fuera de esos temas, responde ÚNICAMENTE: ")
                  .append("'Estoy aquí para ayudarte con tu código y tu proyecto. ¿En qué parte del desarrollo necesitas ayuda?' ")
                  .append("Reglas de respuesta: máximo 3 oraciones para respuestas conversacionales, ")
                  .append("máximo 5 líneas + código para explicaciones. ")
                  .append("Sin introducciones como 'Claro, con gusto'. Ve directo al punto. ")
                  .append("Tu rol es guiar, no resolver: sugiere el siguiente paso, no la solución completa. ")
                  .append("Responde en el idioma del estudiante.\n\n");
        }

        // Include conversation history for context
        if (conversationHistory != null && !conversationHistory.isBlank()) {
            prompt.append("Conversación anterior:\n").append(conversationHistory).append("\n\n");
        }

        // Include current editor code if available
        if (!isExerciseHelp && currentCode != null && !currentCode.isBlank()) {
            prompt.append("Código actual del estudiante (").append(language).append("):\n")
                  .append(currentCode).append("\n\n");
        }

        prompt.append("Estudiante: ").append(message);

        return callGroqApi(prompt.toString());
    }

    /**
     * Analiza el código del estudiante y retorna un análisis estructurado en formato JSON.
     * Actúa como tutor pedagógico, guiando sin dar soluciones directas.
     */
    @Override
    public String analyzeCode(String code, String language, String projectDescription) {
        String prompt = buildAnalysisPrompt(code, language, projectDescription);
        return callGroqApi(prompt, 1500); // Más tokens para respuesta JSON estructurada
    }

    /**
     * Construye el prompt detallado para el análisis pedagógico del código.
     */
    private String buildAnalysisPrompt(String code, String language, String projectDescription) {
        return "You are an intelligent code analysis assistant embedded in a learning-focused code editor." +
                "Your job is to help students understand their own code and guide their next steps." +
                "You are a tutor, not a code generator. Never write the solution for the student.\n\n" +
                
                "═══════════════════════════════════════════INPUT═══════════════════════════════════════════\n" +
                "Language: " + language + "\n" +
                "Project description: " + projectDescription + "\n" +
                "Code:\n" + code + "\n\n" +
                
                "═══════════════════════════════════════════YOUR TEACHING PHILOSOPHY═══════════════════════════════════════════\n" +
                "1. You are a TUTOR, not a code generator.\n" +
                "   - Never write the next function or block of code for the student.\n" +
                "   - Guide with questions and hints, not with solutions.\n\n" +
                
                "2. Be encouraging and constructive.\n" +
                "   - Acknowledge what the student did well before pointing out issues.\n" +
                "   - Use simple, friendly language.\n\n" +
                
                "3. Explanations must be:\n" +
                "   - Specific to the code the student actually wrote.\n" +
                "   - Short and scannable, not walls of text.\n" +
                "   - Written as if talking to a student, not reading documentation.\n\n" +
                
                "4. Suggestions must be:\n" +
                "   - The logical next step given what the student already wrote.\n" +
                "   - Framed as challenges or questions, not instructions.\n" +
                "   - A maximum of 3 suggestions at a time to avoid overwhelming.\n\n" +
                
                "5. If the code is empty or has only a few lines:\n" +
                "   - Encourage the student to start.\n" +
                "   - Suggest only the very first step.\n\n" +
                
                "6. If the code has errors:\n" +
                "   - Point out the error clearly without fixing it.\n" +
                "   - Give a hint about what to look for, not the fix itself.\n\n" +
                
                "═══════════════════════════════════════════OUTPUT FORMAT═══════════════════════════════════════════\n" +
                "You MUST respond ONLY with a valid JSON object.\n" +
                "- No markdown formatting\n" +
                "- No code fences (no ```)\n" +
                "- No text before or after the JSON\n" +
                "- The JSON must be parseable as-is\n\n" +
                
                "The JSON structure is ALWAYS exactly this:\n" +
                "{\n" +
                "  \"summary\": \"string — one sentence describing what the current code does overall\",\n" +
                "  \"blocks\": [\n" +
                "    {\n" +
                "      \"blockName\": \"string — name of the function, class, or block detected\",\n" +
                "      \"blockType\": \"string — function | class | loop | conditional | variable | other\",\n" +
                "      \"explanation\": \"string — plain English explanation of what this block does\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"codeQuality\": {\n" +
                "    \"score\": number — integer from 1 to 5,\n" +
                "    \"feedback\": \"string — one encouraging sentence about the overall code quality\"\n" +
                "  },\n" +
                "  \"suggestions\": [\n" +
                "    {\n" +
                "      \"order\": number — 1, 2 or 3,\n" +
                "      \"title\": \"string — short label for the suggestion\",\n" +
                "      \"description\": \"string — guiding challenge or question, never a direct solution\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"hasErrors\": boolean,\n" +
                "  \"errorHint\": \"string or null — if hasErrors is true, a hint about what to look for\"\n" +
                "}\n\n" +
                
                "═══════════════════════════════════════════BLOCKS DETECTION RULES═══════════════════════════════════════════\n" +
                "- Detect every meaningful block in the code: functions, classes, loops, conditionals.\n" +
                "- Ignore import statements and single variable declarations unless they are particularly important to explain.\n" +
                "- If the code is too short to have named blocks, return an empty array for blocks and explain the code in the summary instead.\n" +
                "- Maximum 8 blocks. If there are more, group minor ones.\n\n" +
                
                "═══════════════════════════════════════════SUGGESTIONS RULES═══════════════════════════════════════════\n" +
                "- Always give between 1 and 3 suggestions. Never more than 3.\n" +
                "- Order them from most immediate to least immediate.\n" +
                "- Base suggestions on the project description provided by the student.\n" +
                "  If the student said they are building a calculator, suggest calculator-related steps.\n" +
                "- Frame every suggestion as a question or challenge, never as an instruction.\n\n" +
                
                "GOOD: \"Your add() method works well. What would happen if the user types a letter instead of a number? How could you handle that?\"\n" +
                "BAD:  \"Add input validation to your add() method using a try-catch block.\"\n\n" +
                
                "═══════════════════════════════════════════SCORE RULES═══════════════════════════════════════════\n" +
                "Score from 1 to 5 based on:\n" +
                "1 — Code is just started or has major structural issues\n" +
                "2 — Basic structure exists but has several problems\n" +
                "3 — Code works for the happy path, needs error handling or cleanup\n" +
                "4 — Code is clean and handles basic edge cases\n" +
                "5 — Code is well structured, readable, and handles edge cases well\n\n" +
                
                "The feedback sentence must always be encouraging even at score 1 or 2.\n\n" +
                
                "═══════════════════════════════════════════VALIDATION CHECKLIST (apply before responding)═══════════════════════════════════════════\n" +
                "[ ] Response is ONLY the JSON object, nothing else\n" +
                "[ ] JSON is valid and parseable\n" +
                "[ ] All required fields are present\n" +
                "[ ] No suggestion gives away code or a direct solution\n" +
                "[ ] Explanations are specific to the actual code, not generic\n" +
                "[ ] errorHint is null when hasErrors is false\n" +
                "[ ] suggestions array has between 1 and 3 items\n" +
                "[ ] score is an integer between 1 and 5";
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
        return callGroqApi(prompt, 300);
    }

    /**
     * Realiza la llamada HTTP a la API de Groq con límite de tokens configurable.
     */
    private String callGroqApi(String prompt, int maxTokens) {
        try {
            // Sanitizar el prompt para que sea JSON válido
            String safePrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\r\n", "\\n")
                    .replace("\r", "\\n")
                    .replace("\n", "\\n")
                    .replace("\t", "\\t");

            // Construir el body en formato OpenAI chat completions
            String requestBody = "{"
                    + "\"model\": \"" + model + "\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \""
                    + safePrompt
                    + "\"}],"
                    + "\"max_tokens\": " + maxTokens
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
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
