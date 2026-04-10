package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for user login.
 * Validates password length and disallows spaces.
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
    @Size(max = 10, message = "La contraseña no puede tener más de 10 caracteres")
    @Pattern(regexp = "^\\S+$", message = "La contraseña no puede contener espacios")
    private String password;
}
