package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO returned to the frontend after creating or fetching a user.
 * Never includes the password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    // NOTE: password is intentionally excluded for security
}