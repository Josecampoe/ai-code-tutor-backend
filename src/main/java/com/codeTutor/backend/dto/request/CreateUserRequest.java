package com.codeTutor.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating a new user.
 * Validates username and password length and disallows spaces.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 10, message = "El nombre de usuario debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "^\\S+$", message = "El nombre de usuario no puede contener espacios")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 10, message = "La contraseña debe tener entre 6 y 10 caracteres")
    @Pattern(regexp = "^\\S+$", message = "La contraseña no puede contener espacios")
    private String password;
}
