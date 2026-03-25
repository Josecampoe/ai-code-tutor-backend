package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.SaveCodeSnapshotRequest;
import com.codeTutor.backend.dto.response.CodeSnapshotResponse;
import com.codeTutor.backend.model.CodeSnapshot;
import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.repository.CodeSnapshotRepository;
import com.codeTutor.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona el historial de versiones del código de cada proyecto.
 * Extiende BaseEntityService para aplicar el patrón Template Method en el guardado de snapshots.
 * El flujo (validar → construir → persistir → responder) está definido en la clase base.
 */
@Service
public class CodeSnapshotService extends BaseEntityService<SaveCodeSnapshotRequest, CodeSnapshotResponse, CodeSnapshot> {

    // Repositorio de snapshots para persistencia en base de datos
    @Autowired
    private CodeSnapshotRepository codeSnapshotRepository;

    // Repositorio de proyectos para verificar que el proyecto existe
    @Autowired
    private ProjectRepository projectRepository;

    // =========================================================
    // IMPLEMENTACIÓN DEL PATRÓN TEMPLATE METHOD
    // =========================================================

    /**
     * Paso 1 — Valida que el proyecto referenciado exista en la base de datos.
     */
    @Override
    protected void validate(SaveCodeSnapshotRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId());
        }
    }

    /**
     * Paso 2 — Construye el CodeSnapshot calculando el número de versión automáticamente.
     */
    @Override
    protected CodeSnapshot buildEntity(SaveCodeSnapshotRequest request) {
        // Obtener el proyecto para asociarlo al snapshot
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));

        // Calcular el número de versión sumando 1 al total existente
        int versionNumber = codeSnapshotRepository.countByProjectId(request.getProjectId()) + 1;

        return CodeSnapshot.builder()
                .content(request.getContent())
                .versionLabel(request.getVersionLabel())
                .versionNumber(versionNumber)
                .project(project)
                .build();
    }

    /**
     * Paso 3 — Persiste el snapshot en la base de datos.
     */
    @Override
    protected CodeSnapshot persist(CodeSnapshot snapshot) {
        return codeSnapshotRepository.save(snapshot);
    }

    /**
     * Paso 4 — Convierte el CodeSnapshot guardado a su DTO de respuesta.
     */
    @Override
    protected CodeSnapshotResponse toResponse(CodeSnapshot snapshot) {
        return CodeSnapshotResponse.builder()
                .id(snapshot.getId())
                .content(snapshot.getContent())
                .versionLabel(snapshot.getVersionLabel())
                .versionNumber(snapshot.getVersionNumber())
                .createdAt(snapshot.getCreatedAt())
                .projectId(snapshot.getProject().getId())
                .build();
    }

    // =========================================================
    // MÉTODOS ADICIONALES
    // =========================================================

    /**
     * Guarda un nuevo snapshot usando el flujo del Template Method heredado.
     * Mantiene el nombre original para compatibilidad con ProjectController.
     */
    public CodeSnapshotResponse saveSnapshot(SaveCodeSnapshotRequest request) {
        // Delegar al Template Method de la clase base
        return create(request);
    }

    /**
     * Retorna el historial completo de versiones de un proyecto, del más antiguo al más reciente.
     */
    public List<CodeSnapshotResponse> getSnapshotsByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + projectId);
        }
        return codeSnapshotRepository.findByProjectIdOrderByVersionNumberAsc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el snapshot más reciente de un proyecto.
     */
    public CodeSnapshotResponse getLatestSnapshot(Long projectId) {
        CodeSnapshot snapshot = codeSnapshotRepository
                .findTopByProjectIdOrderByVersionNumberDesc(projectId)
                .orElseThrow(() -> new RuntimeException("No hay versiones guardadas para el proyecto ID: " + projectId));
        return toResponse(snapshot);
    }
}
