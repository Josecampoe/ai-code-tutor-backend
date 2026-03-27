package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Respuesta al iniciar sesión exitosamente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private String message;
}
