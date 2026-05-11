package com.codeTutor.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.Category;

/**
 * Repository interface for Category entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
