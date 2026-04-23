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
import com.codeTutor.backend.model.AiSession;
import com.codeTutor.backend.service.AIService;
import com.codeTutor.backend.service.ChatSessionService;

import jakarta.validation.Valid;

/**
 * REST controller for the AI chat endpoint.
 * Persists messages to the database and loads history when a project session exists.
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ChatSessionService chatSessionService;

    /**
     * POST /api/chat
     * Sends a message to the AI tutor and persists the conversation.
     * If projectId and userId are provided, uses persistent session history.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String conversationHistory;

        // Use persistent DB history if projectId is provided, otherwise use frontend history
        if (request.getProjectId() != null && request.getUserId() != null) {
            conversationHistory = chatSessionService.getRecentHistoryAsText(request.getProjectId());
        } else if (request.getHistory() != null && !request.getHistory().isEmpty()) {
            conversationHistory = request.getHistory().stream()
                    .map(chatMessage -> (chatMessage.getRole().equals("user") ? "Estudiante" : "Tutor") + ": " + chatMessage.getContent())
                    .collect(Collectors.joining("\n"));
        } else {
            conversationHistory = "";
        }

        String aiResponse = aiService.chat(
                request.getMessage(),
                conversationHistory,
                request.getCurrentCode(),
                request.getLanguage()
        );

        // Persist messages if session context is available
        if (request.getProjectId() != null && request.getUserId() != null) {
            AiSession session = chatSessionService.getOrCreateSession(
                    request.getProjectId(), request.getUserId());
            chatSessionService.saveMessage(session.getId(), "user", request.getMessage());
            chatSessionService.saveMessage(session.getId(), "assistant", aiResponse);
        }

        return ResponseEntity.ok(ChatResponse.builder().message(aiResponse).build());
    }
}
