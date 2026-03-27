package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta combinada para cargar el editor de código.
 * Incluye los datos del proyecto y el último snapshot en una sola llamada,
 * evitando múltiples requests al abrir un proyecto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorLoadResponse {

    private Long projectId;
    private String projectName;
    private String description;
    private String programmingLanguage;
    private Long userId;
    private String username;

    // Último código guardado (null si no hay snapshots aún)
    private String currentCode;
    private Integer currentVersion;
    private String versionLabel;
}
