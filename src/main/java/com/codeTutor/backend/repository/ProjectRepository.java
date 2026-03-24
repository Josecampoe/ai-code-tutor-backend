package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Project entity.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Get all projects belonging to a specific user
    List<Project> findByUserId(Long userId);

    // Get all projects for a user filtered by programming language
    List<Project> findByUserIdAndProgrammingLanguage(Long userId, String language);
}