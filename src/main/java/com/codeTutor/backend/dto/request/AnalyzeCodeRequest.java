package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO recibido desde el frontend cuando el estudiante solicita análisis de su código.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeCodeRequest {

    // El código que el estudiante quiere analizar
    @NotBlank(message = "El código no puede estar vacío")
    private String code;

    // El lenguaje de programación del código
    @NotBlank(message = "El lenguaje es requerido")
    private String language;

    // El proyecto al que pertenece este análisis
    @NotNull(message = "El ID del proyecto es requerido")
    private Long projectId;
}
