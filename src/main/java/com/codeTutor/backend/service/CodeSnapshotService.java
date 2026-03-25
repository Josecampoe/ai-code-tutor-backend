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
 * Permite guardar snapshots del código del estudiante y recuperar su historial.
 */
@Service
public class CodeSnapshotService {

    // Repositorio de snapshots para persistencia en base de datos
    @Autowired
    private CodeSnapshotRepository codeSnapshotRepository;

    // Repositorio de proyectos para verificar que el proyecto existe
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Guarda una nueva versión del código del estudiante para un proyecto dado.
     * El número de versión se asigna automáticamente según cuántas versiones existen.
     */
    public CodeSnapshotResponse saveSnapshot(SaveCodeSnapshotRequest request) {
        // Verificar que el proyecto existe antes de guardar el snapshot
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + request.getProjectId()));

        // Calcular el número de versión sumando 1 al total de versiones existentes
        int versionNumber = codeSnapshotRepository.countByProjectId(request.getProjectId()) + 1;

        // Construir el snapshot con los datos del request
        CodeSnapshot snapshot = CodeSnapshot.builder()
                .content(request.getContent())
                .versionLabel(request.getVersionLabel())
                .versionNumber(versionNumber)
                .project(project)
                .build();

        // Guardar el snapshot en la base de datos
        CodeSnapshot saved = codeSnapshotRepository.save(snapshot);

        return toResponse(saved);
    }

    /**
     * Retorna el historial completo de versiones de un proyecto, ordenado de más antiguo a más reciente.
     */
    public List<CodeSnapshotResponse> getSnapshotsByProject(Long projectId) {
        // Verificar que el proyecto existe
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + projectId);
        }

        // Obtener todos los snapshots ordenados por número de versión ascendente
        return codeSnapshotRepository.findByProjectIdOrderByVersionNumberAsc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el snapshot más reciente de un proyecto.
     */
    public CodeSnapshotResponse getLatestSnapshot(Long projectId) {
        // Buscar el snapshot con el número de versión más alto
        CodeSnapshot snapshot = codeSnapshotRepository
                .findTopByProjectIdOrderByVersionNumberDesc(projectId)
                .orElseThrow(() -> new RuntimeException("No hay versiones guardadas para el proyecto ID: " + projectId));

        return toResponse(snapshot);
    }

    /**
     * Convierte un CodeSnapshot a su DTO de respuesta.
     */
    private CodeSnapshotResponse toResponse(CodeSnapshot snapshot) {
        return CodeSnapshotResponse.builder()
                .id(snapshot.getId())
                .content(snapshot.getContent())
                .versionLabel(snapshot.getVersionLabel())
                .versionNumber(snapshot.getVersionNumber())
                .createdAt(snapshot.getCreatedAt())
                .projectId(snapshot.getProject().getId())
                .build();
    }
}
