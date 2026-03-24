package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Spring Data JPA auto-implements CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email (used for login)
    Optional<User> findByEmail(String email);

    // Find a user by their username
    Optional<User> findByUsername(String username);

    // Check if a user with that email already exists
    boolean existsByEmail(String email);
}