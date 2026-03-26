package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.EvaluateSolutionRequest;
import com.codeTutor.backend.dto.request.GenerateExerciseRequest;
import com.codeTutor.backend.dto.response.EvaluationResponse;
import com.codeTutor.backend.dto.response.ExerciseResponse;
import com.codeTutor.backend.dto.response.LearningTopicResponse;
import com.codeTutor.backend.dto.response.StudentProgressResponse;
import com.codeTutor.backend.model.Exercise;
import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.model.StudentProgress;
import com.codeTutor.backend.model.User;
import com.codeTutor.backend.repository.ExerciseRepository;
import com.codeTutor.backend.repository.LearningTopicRepository;
import com.codeTutor.backend.repository.StudentProgressRepository;
import com.codeTutor.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal del módulo "Aprende con IA".
 * Coordina la generación de ejercicios, evaluación de soluciones,
 * pistas y seguimiento del progreso del estudiante.
 * Delega las llamadas de IA a AIService.
 */
@Service
public class LearningService {

    // Servicio de IA para generar ejercicios y evaluar soluciones
    @Autowired
    private AIService aiService;

    // Repositorio de temas de aprendizaje
    @Autowired
    private LearningTopicRepository topicRepository;

    // Repositorio de ejercicios generados
    @Autowired
    private ExerciseRepository exerciseRepository;

    // Repositorio de progreso del estudiante
    @Autowired
    private StudentProgressRepository progressRepository;

    // Repositorio de usuarios para verificar existencia
    @Autowired
    private UserRepository userRepository;

    // =========================================================
    // TEMAS DE APRENDIZAJE
    // =========================================================

    /**
     * Retorna todos los temas de aprendizaje disponibles.
     */
    public List<LearningTopicResponse> getAllTopics() {
        return topicRepository.findAll()
                .stream()
                .map(this::toTopicResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna los temas filtrados por categoría.
     * Categorías válidas: DATA_STRUCTURE, DESIGN_PATTERN, ALGORITHM
     */
    public List<LearningTopicResponse> getTopicsByCategory(String category) {
        return topicRepository.findByCategory(category.toUpperCase())
                .stream()
                .map(this::toTopicResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GENERACIÓN DE EJERCICIOS CON IA
    // =========================================================

    /**
     * Genera un ejercicio nuevo con IA para el tema y lenguaje solicitados.
     * Persiste el ejercicio en la base de datos y lo retorna al estudiante.
     */
    public ExerciseResponse generateExercise(GenerateExerciseRequest request) {
        // Verificar que el tema existe
        LearningTopic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Tema no encontrado con ID: " + request.getTopicId()));

        // Pedir a la IA que genere el enunciado del ejercicio
        String statement = aiService.generateExerciseStatement(topic.getName(), topic.getCategory(), request.getLanguage());

        // Pedir a la IA que genere el código de inicio (esqueleto)
        String starterCode = aiService.generateStarterCode(topic.getName(), request.getLanguage());

        // Persistir el ejercicio generado
        Exercise exercise = Exercise.builder()
                .statement(statement)
                .starterCode(starterCode)
                .language(request.getLanguage())
                .topic(topic)
                .build();

        Exercise saved = exerciseRepository.save(exercise);
        return toExerciseResponse(saved);
    }

    /**
     * Retorna todos los ejercicios disponibles para un tema y lenguaje específicos.
     */
    public List<ExerciseResponse> getExercisesByTopicAndLanguage(Long topicId, String language) {
        return exerciseRepository.findByTopicIdAndLanguage(topicId, language)
                .stream()
                .map(this::toExerciseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna una pista para el ejercicio sin revelar la solución completa.
     */
    public String getHint(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con ID: " + exerciseId));

        return aiService.generateHint(exercise.getStatement(), exercise.getLanguage());
    }

    // =========================================================
    // EVALUACIÓN DE SOLUCIONES CON IA
    // =========================================================

    /**
     * Evalúa la solución del estudiante con IA.
     * Si es correcta, incrementa el contador de ejercicios completados.
     */
    public EvaluationResponse evaluateSolution(EvaluateSolutionRequest request) {
        // Verificar que el ejercicio existe
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con ID: " + request.getExerciseId()));

        // Verificar que el usuario existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));

        // Pedir a la IA que evalúe la solución
        String rawFeedback = aiService.evaluateSolution(
                exercise.getStatement(),
                request.getSolutionCode(),
                request.getLanguage()
        );

        // Determinar si la solución es correcta basándose en la respuesta de la IA
        boolean isCorrect = rawFeedback.toLowerCase().contains("correcto") ||
                rawFeedback.toLowerCase().contains("correct") ||
                rawFeedback.toLowerCase().contains("bien hecho") ||
                rawFeedback.toLowerCase().contains("excelente");

        // Pedir sugerencia de mejora si la solución es correcta pero mejorable
        String improvement = aiService.suggestNextStep(request.getSolutionCode(), request.getLanguage());

        // Actualizar el progreso del estudiante si la solución es correcta
        int exercisesCompleted = updateProgress(user, exercise.getTopic(), isCorrect);

        return EvaluationResponse.builder()
                .correct(isCorrect)
                .feedback(rawFeedback)
                .improvement(improvement)
                .exercisesCompleted(exercisesCompleted)
                .build();
    }

    // =========================================================
    // PROGRESO DEL ESTUDIANTE
    // =========================================================

    /**
     * Retorna todo el progreso de un estudiante en todos los temas.
     */
    public List<StudentProgressResponse> getStudentProgress(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        return progressRepository.findByUserId(userId)
                .stream()
                .map(this::toProgressResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el progreso de un estudiante en un tema específico.
     */
    public StudentProgressResponse getProgressByTopic(Long userId, Long topicId) {
        StudentProgress progress = progressRepository.findByUserIdAndTopicId(userId, topicId)
                .orElse(null);

        if (progress == null) {
            // Si no hay progreso aún, retornar uno vacío
            LearningTopic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Tema no encontrado con ID: " + topicId));
            return StudentProgressResponse.builder()
                    .userId(userId)
                    .topicId(topicId)
                    .topicName(topic.getName())
                    .topicCategory(topic.getCategory())
                    .exercisesCompleted(0)
                    .status("NOT_STARTED")
                    .build();
        }

        return toProgressResponse(progress);
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================

    /**
     * Actualiza el progreso del estudiante en un tema.
     * Si la solución fue correcta, incrementa el contador.
     * Retorna el total de ejercicios completados.
     */
    private int updateProgress(User user, LearningTopic topic, boolean isCorrect) {
        StudentProgress progress = progressRepository
                .findByUserIdAndTopicId(user.getId(), topic.getId())
                .orElse(StudentProgress.builder()
                        .user(user)
                        .topic(topic)
                        .exercisesCompleted(0)
                        .status("IN_PROGRESS")
                        .build());

        if (isCorrect) {
            progress.setExercisesCompleted(progress.getExercisesCompleted() + 1);
        }

        progressRepository.save(progress);
        return progress.getExercisesCompleted();
    }

    /**
     * Convierte un LearningTopic a su DTO de respuesta.
     */
    private LearningTopicResponse toTopicResponse(LearningTopic topic) {
        return LearningTopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .category(topic.getCategory())
                .description(topic.getDescription())
                .difficulty(topic.getDifficulty())
                .build();
    }

    /**
     * Convierte un Exercise a su DTO de respuesta.
     */
    private ExerciseResponse toExerciseResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .statement(exercise.getStatement())
                .starterCode(exercise.getStarterCode())
                .language(exercise.getLanguage())
                .topicId(exercise.getTopic().getId())
                .topicName(exercise.getTopic().getName())
                .createdAt(exercise.getCreatedAt())
                .build();
    }

    /**
     * Convierte un StudentProgress a su DTO de respuesta.
     */
    private StudentProgressResponse toProgressResponse(StudentProgress progress) {
        return StudentProgressResponse.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .topicId(progress.getTopic().getId())
                .topicName(progress.getTopic().getName())
                .topicCategory(progress.getTopic().getCategory())
                .exercisesCompleted(progress.getExercisesCompleted())
                .status(progress.getStatus())
                .lastActivity(progress.getLastActivity())
                .build();
    }
}
