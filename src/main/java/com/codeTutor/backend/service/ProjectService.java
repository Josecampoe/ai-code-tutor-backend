package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.CreateProjectRequest;
import com.codeTutor.backend.dto.response.ProjectResponse;
import com.codeTutor.backend.exception.ForbiddenException;
import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.model.User;
import com.codeTutor.backend.repository.ProjectRepository;
import com.codeTutor.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de proyectos del estudiante.
 * Maneja creación, consulta y actualización de proyectos.
 * Implementa el patrón Command para operaciones de deshacer/rehacer.
 */
@Service
public class ProjectService {

    /**
     * Justificación del patrón Command:
     * Se utiliza Command porque el editor necesita soportar undo/redo.
     * Cada acción se encapsula como objeto, permitiendo revertirla o repetirla
     * sin acoplar la lógica al servicio principal.
     */

    // Repositorio de proyectos para persistencia
    @Autowired
    private ProjectRepository projectRepository;

    // Repositorio de usuarios para verificar que el dueño del proyecto existe
    @Autowired
    private UserRepository userRepository;

    /**
     * Historial de versiones del código en memoria.
     * LinkedList por su inserción eficiente al final y recorrido secuencial.
     */
    private LinkedList<String> versionHistory = new LinkedList<>();

    /**
     * Pila de comandos ejecutados para deshacer (LIFO).
     */
    private Stack<EditorCommand> undoStack = new Stack<>();

    /**
     * Pila de comandos deshechos para rehacer (LIFO).
     */
    private Stack<EditorCommand> redoStack = new Stack<>();

    // =========================================================
    // PATRÓN COMMAND
    // =========================================================

    public interface EditorCommand {
        void execute();
        void undo();
    }

    public static class WriteCommand implements EditorCommand {
        private final String textToWrite;
        private final String previousState;
        private final StringBuilder currentCode;

        public WriteCommand(String textToWrite, String previousState, StringBuilder currentCode) {
            this.textToWrite = textToWrite;
            this.previousState = previousState;
            this.currentCode = currentCode;
        }

        @Override
        public void execute() {
            currentCode.append(textToWrite);
        }

        @Override
        public void undo() {
            currentCode.setLength(0);
            currentCode.append(previousState);
        }
    }

    public static class DeleteCommand implements EditorCommand {
        private final String deletedText;
        private final int position;
        private final StringBuilder currentCode;

        public DeleteCommand(String deletedText, int position, StringBuilder currentCode) {
            this.deletedText = deletedText;
            this.position = position;
            this.currentCode = currentCode;
        }

        @Override
        public void execute() {
            int end = position + deletedText.length();
            if (position >= 0 && end <= currentCode.length()) {
                currentCode.delete(position, end);
            }
        }

        @Override
        public void undo() {
            if (position >= 0 && position <= currentCode.length()) {
                currentCode.insert(position, deletedText);
            }
        }
    }

    // =========================================================
    // MÉTODOS PRINCIPALES
    // =========================================================

    public ProjectResponse createProject(CreateProjectRequest request) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Authentication required");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + currentUserId));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .programmingLanguage(request.getProgrammingLanguage())
                .user(user)
                .build();

        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        verifyOwnership(project);
        return toResponse(project);
    }

    /**
     * Retorna todos los proyectos de un usuario específico.
     */
    public List<ProjectResponse> getProjectsByUser(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        return projectRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el objeto Project completo (para uso interno de otros servicios).
     */
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));
    }

    /**
     * Actualiza el código de un proyecto guardando la versión anterior en el historial.
     */
    public ProjectResponse updateProjectCode(Long id, String newCode) {
        Project project = getProject(id);

        // Guardar el snapshot del código anterior en el historial en memoria
        if (project.getCodeSnapshots() != null && !project.getCodeSnapshots().isEmpty()) {
            String lastCode = project.getCodeSnapshots()
                    .get(project.getCodeSnapshots().size() - 1)
                    .getContent();
            versionHistory.add(lastCode);
        }

        project.setCurrentCode(newCode);
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    /**
     * Ejecuta un comando y lo agrega a la pila de deshacer.
     */
    public void executeCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Deshace el último comando ejecutado.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Rehace el último comando deshecho.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            EditorCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    /**
     * Retorna el historial de versiones en memoria.
     */
    public List<String> getVersionHistory() {
        return versionHistory;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }

    private void verifyOwnership(Project project) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && !project.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("Access denied");
        }
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .programmingLanguage(project.getProgrammingLanguage())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .userId(project.getUser().getId())
                .username(project.getUser().getUsername())
                .build();
    }
}
