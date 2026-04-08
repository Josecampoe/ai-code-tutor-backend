package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.AiMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiMessageRepository extends JpaRepository<AiMessage, Long> {
    List<AiMessage> findTop10BySessionIdOrderByCreatedAtAsc(Long sessionId);
    List<AiMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
