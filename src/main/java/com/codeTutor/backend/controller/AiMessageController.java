package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.model.AiMessage;
import com.codeTutor.backend.repository.AiMessageRepository;

/**
 * REST controller for the ai_messages table.
 * Retrieves chat messages from AI tutoring sessions.
 */
@RestController
@RequestMapping("/api/messages")
public class AiMessageController {

    @Autowired
    private AiMessageRepository aiMessageRepository;

    /** GET /api/messages/session/{sessionId} — Get all messages in a session */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AiMessage>> getMessagesBySession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(
                aiMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    /** GET /api/messages/session/{sessionId}/recent — Get last 10 messages */
    @GetMapping("/session/{sessionId}/recent")
    public ResponseEntity<List<AiMessage>> getRecentMessages(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(
                aiMessageRepository.findTop10BySessionIdOrderByCreatedAtAsc(sessionId));
    }
}
