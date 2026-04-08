package com.codeTutor.backend.service;

import com.codeTutor.backend.model.AiMessage;
import com.codeTutor.backend.model.AiSession;
import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.model.User;
import com.codeTutor.backend.repository.AiMessageRepository;
import com.codeTutor.backend.repository.AiSessionRepository;
import com.codeTutor.backend.repository.ProjectRepository;
import com.codeTutor.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages AI chat sessions and persists messages to the database.
 * Enables conversation history to be loaded when a student resumes a project.
 */
@Service
public class ChatSessionService {

    @Autowired
    private AiSessionRepository sessionRepository;

    @Autowired
    private AiMessageRepository messageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // Gets the latest session for a project or creates a new one
    public AiSession getOrCreateSession(Long projectId, Long userId) {
        return sessionRepository.findTopByProjectIdOrderByStartedAtDesc(projectId)
                .orElseGet(() -> createSession(projectId, userId));
    }

    // Creates a new session for the given project and user
    public AiSession createSession(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        AiSession session = AiSession.builder()
                .project(project)
                .user(user)
                .messageCount(0)
                .build();
        return sessionRepository.save(session);
    }

    // Persists a user or assistant message to the session
    public void saveMessage(Long sessionId, String role, String content) {
        AiSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada: " + sessionId));

        AiMessage message = AiMessage.builder()
                .session(session)
                .role(role)
                .content(content)
                .build();
        messageRepository.save(message);

        session.setMessageCount(session.getMessageCount() + 1);
        sessionRepository.save(session);
    }

    // Returns the last 10 messages of the latest session for a project
    public String getRecentHistoryAsText(Long projectId) {
        return sessionRepository.findTopByProjectIdOrderByStartedAtDesc(projectId)
                .map(session -> {
                    List<AiMessage> messages = messageRepository
                            .findTop10BySessionIdOrderByCreatedAtAsc(session.getId());
                    return messages.stream()
                            .map(m -> (m.getRole().equals("user") ? "Estudiante" : "Tutor") + ": " + m.getContent())
                            .collect(Collectors.joining("\n"));
                })
                .orElse("");
    }
}
