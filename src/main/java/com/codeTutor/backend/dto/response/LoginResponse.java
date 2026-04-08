package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Response returned after successful login.
 * Includes JWT token for subsequent authenticated requests.
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
    private String token;
    private String message;
}
