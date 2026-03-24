package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.CodeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CodeSnapshot entity.
 * Handles version history of code per project.
 */
@Repository
public interface CodeSnapshotRepository extends JpaRepository<CodeSnapshot, Long> {

    // Get all snapshots for a project ordered by version (oldest to newest)
    List<CodeSnapshot> findByProjectIdOrderByVersionNumberAsc(Long projectId);

    // Get the latest snapshot of a project
    Optional<CodeSnapshot> findTopByProjectIdOrderByVersionNumberDesc(Long projectId);

    // Count how many versions a project has
    int countByProjectId(Long projectId);
}