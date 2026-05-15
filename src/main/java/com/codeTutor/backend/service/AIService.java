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
     */
    @Override
    public String analyzeCode(String code, String language, String projectDescription) {
        String prompt = buildAnalysisPrompt(code, language, projectDescription);
        return callGroqApi(prompt, 1500);
    }

    /**
     * Generates a complete lesson in JSON format for a specific lesson.
     */
    @Override
    public String generateLessonContent(String language, String level, int lessonNumber, String title) {
        String levelGuidance = switch (level.toLowerCase()) {
            case "beginner" -> "Use real-world analogies and everyday language. Avoid jargon. " +
                    "Explain every concept as if the student has never coded before. " +
                    "Break down each idea into tiny, digestible pieces.";
            case "intermediate" -> "Use standard programming terminology. Connect new concepts to things " +
                    "the student already knows. Focus on correct usage, common patterns, and best practices.";
            case "advanced" -> "Discuss trade-offs, performance implications, and edge cases. " +
                    "Compare alternative approaches. Assume solid programming fundamentals.";
            default -> "Use clear, practical explanations appropriate for the level.";
        };

        String prompt = """
            You are an elite programming tutor crafting a single high-quality lesson for the "Learn with AI" platform.

            TASK:
            Language: __LANGUAGE__
            Level: __LEVEL__
            Lesson __LESSON_NUMBER__ of 10
            Title: __LESSON_TITLE__

            Generate a complete lesson about "__LESSON_TITLE__" in __LANGUAGE__ for a __LEVEL__ student.
            The lesson must teach ONE specific concept deeply. Do not cover multiple unrelated topics.

            LEVEL GUIDANCE:
            __LEVEL_GUIDANCE__

            OUTPUT FORMAT:
            Return ONLY valid JSON. No markdown, no code fences (no ```), no text before or after the JSON.

            JSON SCHEMA (exactly 5 sections in this order):
            {
              "title": "string — specific, engaging lesson title",
              "summary": "string — one sentence about measurable learning outcome",
              "estimatedMinutes": integer — between 5 and 20,
              "sections": [
                {"type": "explanation", "title": "string", "content": "string", "code": null, "prompt": null, "hints": null},
                {"type": "example", "title": "string", "content": "string", "code": "string — working __LANGUAGE__ code", "prompt": null, "hints": null},
                {"type": "explanation", "title": "string", "content": "string — deeper dive", "code": null, "prompt": null, "hints": null},
                {"type": "tip", "title": "string", "content": "string — most common mistake", "code": null, "prompt": null, "hints": null},
                {"type": "exercise", "title": "string", "content": "string — setup", "code": null, "prompt": "string — clear task", "hints": ["guiding q1", "guiding q2", "guiding q3"]}
              ]
            }

            SECTION REQUIREMENTS:

            1. FIRST EXPLANATION (type: "explanation"):
            - Start with WHY this concept matters before HOW it works
            - Use a real-world analogy the student can visualize
            - Define every new term the first time you use it
            - Maximum 150 words. NO code in this section.

            2. CODE EXAMPLE (type: "example"):
            - Write working __LANGUAGE__ code that the student could actually run
            - Include comments to explain key lines
            - Maximum 25 lines. Directly illustrate the concept from section 1.
            - After the code, write 2-3 sentences explaining what the code does.

            3. DEEP DIVE (type: "explanation"):
            - Explore a variation, edge case, or more advanced usage
            - Answer "what happens if..." or "how is this different from..."
            - Maximum 150 words. NO code unless essential.

            4. TIP — COMMON MISTAKE (type: "tip"):
            - Identify the MOST COMMON mistake beginners make with this concept
            - Explain why it fails conceptually and how to fix it
            - Maximum 100 words.

            5. EXERCISE (type: "exercise"):
            - Give a clear, focused programming task that requires APPLYING the concept
            - Must be solvable in 5-10 minutes
            - Include exactly 3 hints. Each hint is a GUIDING QUESTION, never a solution.
            - BAD hint: "Use a for loop"
            - GOOD hint: "What if you needed to repeat this action for every element?"

            QUALITY RULES:

            1. Be SPECIFIC to __LANGUAGE__. Use __LANGUAGE__-specific syntax and conventions.
               Bad: "Variables store data." Good: "Think of a variable as a labeled jar where you can store a single value."

            2. Every code example must be syntactically correct for __LANGUAGE__.

            3. Section titles must be engaging. Bad: "Introduction". Good: "What problem does X solve?"

            4. If __LEVEL__ is "beginner", explain every term. Do not use jargon without definition.

            5. If __LEVEL__ is "advanced", assume syntax knowledge and focus on deeper understanding.

            6. The JSON title field should be captivating but informative.

            7. All content must be in English.

            8. estimatedMinutes must be realistic — 5 for simple concepts, up to 20 for complex ones.
            """
            .replace("__LANGUAGE__", language)
            .replace("__LEVEL__", level)
            .replace("__LESSON_NUMBER__", String.valueOf(lessonNumber))
            .replace("__LESSON_TITLE__", title)
            .replace("__LEVEL_GUIDANCE__", levelGuidance);

        String response = callGroqApi(prompt, 3000);
        return cleanJsonResponse(response);
    }

    private String cleanJsonResponse(String content) {
        if (content == null) return null;
        content = content.trim();
        if (content.startsWith("```json")) content = content.substring(7);
        else if (content.startsWith("```")) content = content.substring(3);
        if (content.endsWith("```")) content = content.substring(0, content.length() - 3);
        return content.trim();
    }

    /**
     * Construye el prompt detallado para el análisis pedagógico del código.
     */
    private String buildAnalysisPrompt(String code, String language, String projectDescription) {
        String template = """
                You are an intelligent code analysis assistant embedded in a learning-focused code editor.\
                Your job is to help students understand their own code and guide their next steps.\
                You are a tutor, not a code generator. Never write the solution for the student.

                ═══════════════════════════════════════════INPUT═══════════════════════════════════════════
                Language: %s
                Project description: %s
                Code:
                %s

                ═══════════════════════════════════════════YOUR TEACHING PHILOSOPHY═══════════════════════════════════════════
                1. You are a TUTOR, not a code generator.
                   - Never write the next function or block of code for the student.
                   - Guide with questions and hints, not with solutions.

                2. Be encouraging and constructive.
                   - Acknowledge what the student did well before pointing out issues.
                   - Use simple, friendly language.

                3. Explanations must be:
                   - Specific to the code the student actually wrote.
                   - Short and scannable, not walls of text.
                   - Written as if talking to a student, not reading documentation.

                4. Suggestions must be:
                   - The logical next step given what the student already wrote.
                   - Framed as challenges or questions, not instructions.
                   - A maximum of 3 suggestions at a time to avoid overwhelming.

                5. If the code is empty or has only a few lines:
                   - Encourage the student to start.
                   - Suggest only the very first step.

                6. If the code has errors:
                   - Point out the error clearly without fixing it.
                   - Give a hint about what to look for, not the fix itself.

                ═══════════════════════════════════════════OUTPUT FORMAT═══════════════════════════════════════════
                You MUST respond ONLY with a valid JSON object.
                - No markdown formatting
                - No code fences (no ```)
                - No text before or after the JSON
                - The JSON must be parseable as-is

                The JSON structure is ALWAYS exactly this:
                {
                  "summary": "string — one sentence describing what the current code does overall",
                  "blocks": [
                    {
                      "blockName": "string — name of the function, class, or block detected",
                      "blockType": "string — function | class | loop | conditional | variable | other",
                      "explanation": "string — plain English explanation of what this block does"
                    }
                  ],
                  "codeQuality": {
                    "score": number — integer from 1 to 5,
                    "feedback": "string — one encouraging sentence about the overall code quality"
                  },
                  "suggestions": [
                    {
                      "order": number — 1, 2 or 3,
                      "title": "string — short label for the suggestion",
                      "description": "string — guiding challenge or question, never a direct solution"
                    }
                  ],
                  "hasErrors": boolean,
                  "errorHint": "string or null — if hasErrors is true, a hint about what to look for"
                }

                ═══════════════════════════════════════════BLOCKS DETECTION RULES═══════════════════════════════════════════
                - Detect every meaningful block in the code: functions, classes, loops, conditionals.
                - Ignore import statements and single variable declarations unless they are particularly important to explain.
                - If the code is too short to have named blocks, return an empty array for blocks and explain the code in the summary instead.
                - Maximum 8 blocks. If there are more, group minor ones.

                ═══════════════════════════════════════════SUGGESTIONS RULES═══════════════════════════════════════════
                - Always give between 1 and 3 suggestions. Never more than 3.
                - Order them from most immediate to least immediate.
                - Base suggestions on the project description provided by the student.
                  If the student said they are building a calculator, suggest calculator-related steps.
                - Frame every suggestion as a question or challenge, never as an instruction.

                GOOD: "Your add() method works well. What would happen if the user types a letter instead of a number? How could you handle that?"
                BAD:  "Add input validation to your add() method using a try-catch block."

                ═══════════════════════════════════════════SCORE RULES═══════════════════════════════════════════
                Score from 1 to 5 based on:
                1 — Code is just started or has major structural issues
                2 — Basic structure exists but has several problems
                3 — Code works for the happy path, needs error handling or cleanup
                4 — Code is clean and handles basic edge cases
                5 — Code is well structured, readable, and handles edge cases well

                The feedback sentence must always be encouraging even at score 1 or 2.

                ═══════════════════════════════════════════VALIDATION CHECKLIST (apply before responding)═══════════════════════════════════════════
                [ ] Response is ONLY the JSON object, nothing else
                [ ] JSON is valid and parseable
                [ ] All required fields are present
                [ ] No suggestion gives away code or a direct solution
                [ ] Explanations are specific to the actual code, not generic
                [ ] errorHint is null when hasErrors is false
                [ ] suggestions array has between 1 and 3 items
                [ ] score is an integer between 1 and 5""";
        return String.format(template, language, projectDescription, code);
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

        } catch (java.io.IOException | InterruptedException e) {
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
