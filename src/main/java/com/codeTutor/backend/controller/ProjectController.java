package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.request.CreateProjectRequest;
import com.codeTutor.backend.dto.request.SaveCodeSnapshotRequest;
import com.codeTutor.backend.dto.response.CodeSnapshotResponse;
import com.codeTutor.backend.dto.response.EditorLoadResponse;
import com.codeTutor.backend.dto.response.ProjectResponse;
import com.codeTutor.backend.service.CodeSnapshotService;
import com.codeTutor.backend.service.ProjectService;

import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de proyectos y su historial de versiones.
 * Expone endpoints para crear proyectos, consultarlos y guardar snapshots de código.
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    // Servicio de proyectos inyectado
    @Autowired
    private ProjectService projectService;

    // Servicio de snapshots inyectado
    @Autowired
    private CodeSnapshotService codeSnapshotService;

    /**
     * POST /api/projects
     * Crea un nuevo proyecto para un estudiante.
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/projects/{id}
     * Retorna un proyecto por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/projects/user/{userId}
     * Retorna todos los proyectos de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByUser(@PathVariable Long userId) {
        List<ProjectResponse> response = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/projects/snapshots
     * Guarda una nueva versión del código del estudiante.
     */
    @PostMapping("/snapshots")
    public ResponseEntity<CodeSnapshotResponse> saveSnapshot(@Valid @RequestBody SaveCodeSnapshotRequest request) {
        CodeSnapshotResponse response = codeSnapshotService.saveSnapshot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/projects/{id}/snapshots
     * Retorna el historial completo de versiones de un proyecto.
     */
    @GetMapping("/{id}/snapshots")
    public ResponseEntity<List<CodeSnapshotResponse>> getSnapshots(@PathVariable Long id) {
        List<CodeSnapshotResponse> response = codeSnapshotService.getSnapshotsByProject(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/projects/{id}/snapshots/latest
     * Retorna el snapshot más reciente de un proyecto.
     */
    @GetMapping("/{id}/snapshots/latest")
    public ResponseEntity<CodeSnapshotResponse> getLatestSnapshot(@PathVariable Long id) {
        CodeSnapshotResponse response = codeSnapshotService.getLatestSnapshot(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/projects/{id}/editor
     * Carga el proyecto con su último código en una sola llamada.
     * Usado por el editor al abrir un proyecto.
     */
    @GetMapping("/{id}/editor")
    public ResponseEntity<EditorLoadResponse> loadEditor(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);

        // Intentar obtener el último snapshot — puede no existir aún
        String currentCode = null;
        Integer currentVersion = null;
        String versionLabel = null;

        try {
            CodeSnapshotResponse latest = codeSnapshotService.getLatestSnapshot(id);
            currentCode = latest.getContent();
            currentVersion = latest.getVersionNumber();
            versionLabel = latest.getVersionLabel();
        } catch (RuntimeException ignored) {
            // No hay snapshots aún — el editor arranca vacío
        }

        EditorLoadResponse response = EditorLoadResponse.builder()
                .projectId(project.getId())
                .projectName(project.getName())
                .description(project.getDescription())
                .language(project.getProgrammingLanguage())
                .userId(project.getUserId())
                .username(project.getUsername())
                .currentCode(currentCode)
                .versionNumber(currentVersion)
                .versionLabel(versionLabel)
                .build();

        return ResponseEntity.ok(response);
    }
}
