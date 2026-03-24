package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.AnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for AnalysisHistory entity.
 * Retrieves past AI analysis results for a project.
 */
@Repository
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {

    // Get full analysis history for a project (most recent first)
    List<AnalysisHistory> findByProjectIdOrderByAnalyzedAtDesc(Long projectId);

    // Get only the last N analyses for a project
    List<AnalysisHistory> findTop5ByProjectIdOrderByAnalyzedAtDesc(Long projectId);
}