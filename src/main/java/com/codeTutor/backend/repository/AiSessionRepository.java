package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.AiSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AiSessionRepository extends JpaRepository<AiSession, Long> {
    Optional<AiSession> findTopByProjectIdOrderByStartedAtDesc(Long projectId);
}
