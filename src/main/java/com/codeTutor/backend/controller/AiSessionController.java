package com.codeTutor.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.model.AiSession;
import com.codeTutor.backend.service.ChatSessionService;

/**
 * REST controller for the ai_sessions table.
 * Manages AI tutoring sessions linked to a project and user.
 */
@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class AiSessionController {

    @Autowired
    private ChatSessionService chatSessionService;

    /** POST /api/sessions/project/{projectId}/user/{userId} — Start or get a session */
    @PostMapping("/project/{projectId}/user/{userId}")
    public ResponseEntity<AiSession> getOrCreateSession(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        AiSession session = chatSessionService.getOrCreateSession(projectId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(session);
    }
}
