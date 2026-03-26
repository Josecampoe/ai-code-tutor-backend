package com.codeTutor.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeTutor.backend.model.StudentProgress;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    // Buscar todo el progreso de un usuario
    List<StudentProgress> findByUserId(Long userId);

    // Buscar el progreso de un usuario en un tema específico
    Optional<StudentProgress> findByUserIdAndTopicId(Long userId, Long topicId);
}
