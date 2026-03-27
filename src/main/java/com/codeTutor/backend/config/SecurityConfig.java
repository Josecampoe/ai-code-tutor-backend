package com.codeTutor.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Provides BCryptPasswordEncoder as a Spring Bean.
 * BCrypt automatically handles salting and uses a cost factor of 10 by default,
 * making brute-force attacks computationally expensive.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
