package com.codeTutor.backend.repository;

import com.codeTutor.backend.model.TerminalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerminalHistoryRepository extends JpaRepository<TerminalHistory, Long> {
    List<TerminalHistory> findTop5ByProjectIdOrderByExecutedAtDesc(Long projectId);
}
