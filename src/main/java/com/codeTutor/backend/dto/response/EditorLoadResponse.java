package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta combinada para cargar el editor de código.
 * Incluye los datos del proyecto y el último snapshot en una sola llamada.
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
    private String language;          // frontend espera "language"
    private Long userId;
    private String username;

    private String currentCode;
    private Integer versionNumber;    // frontend espera "versionNumber"
    private String versionLabel;
}
