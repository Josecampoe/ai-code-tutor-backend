package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para el análisis pedagógico de código.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeCodeRequest {

    @NotBlank(message = "El código no puede estar vacío")
    private String code;

    @NotBlank(message = "El lenguaje de programación es requerido")
    private String language;

    @NotBlank(message = "La descripción del proyecto es requerida")
    private String projectDescription;
}
