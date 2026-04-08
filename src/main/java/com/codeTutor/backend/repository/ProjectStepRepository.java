package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.ProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectStepRepository extends JpaRepository<ProjectStep, Long> {
    List<ProjectStep> findByProjectIdOrderByStepNumberAsc(Long projectId);
}
