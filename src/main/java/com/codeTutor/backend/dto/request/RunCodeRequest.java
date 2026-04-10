package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request to execute code in the integrated terminal.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RunCodeRequest {

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "El lenguaje es obligatorio")
    private String language; // java, python, javascript, typescript

    private String stdin; // optional user input
}
