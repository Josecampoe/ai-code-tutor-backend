package com.codeTutor.backend.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.request.ChatRequest;
import com.codeTutor.backend.dto.response.ChatResponse;
import com.codeTutor.backend.service.AIService;

import jakarta.validation.Valid;

/**
 * Controlador REST para el chat conversacional con el tutor IA.
 * Expone el endpoint que el frontend usa para el panel de chat.
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AIService aiService;

    /**
     * POST /api/chat
     * Recibe el mensaje del estudiante y el historial de conversación,
     * retorna la respuesta contextual del tutor IA.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        // Construir el historial como texto para pasarlo a la IA
        String conversationHistory = "";
        if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            conversationHistory = request.getHistory().stream()
                    .map(m -> (m.getRole().equals("user") ? "Estudiante" : "Tutor") + ": " + m.getContent())
                    .collect(Collectors.joining("\n"));
        }

        String response = aiService.chat(
                request.getMessage(),
                conversationHistory,
                request.getCurrentCode(),
                request.getLanguage()
        );

        return ResponseEntity.ok(ChatResponse.builder().message(response).build());
    }
}
