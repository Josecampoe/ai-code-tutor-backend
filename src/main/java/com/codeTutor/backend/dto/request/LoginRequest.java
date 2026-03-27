package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para el inicio de sesión del usuario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
