package com.codeTutor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

	public static void main(String[] args) {
		validateEnvVars();
		SpringApplication.run(BackendApplication.class, args);
	}

	private static void validateEnvVars() {
		String[] required = {"DATABASE_URL", "JWT_SECRET", "ALLOWED_ORIGINS"};
		for (String var : required) {
			String value = System.getenv(var);
			if (value == null || value.isBlank()) {
				log.error("Missing required env var: {}", var);
				throw new IllegalStateException("Missing required env var: " + var);
			}
		}
		if (System.getenv("JWT_SECRET") != null && System.getenv("JWT_SECRET").length() < 32) {
			log.error("JWT_SECRET must be at least 32 characters long");
			throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
		}
	}
}
