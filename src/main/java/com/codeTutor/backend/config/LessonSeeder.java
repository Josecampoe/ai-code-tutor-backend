package com.codeTutor.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.model.Lesson;
import com.codeTutor.backend.repository.LearningTopicRepository;
import com.codeTutor.backend.repository.LessonRepository;
import com.codeTutor.backend.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed-lessons", havingValue = "true")
public class LessonSeeder implements CommandLineRunner {

    private static final String[] BEGINNER_TITLES = {
        "Introduction", "Variables and Data Types", "Operators",
        "Control Flow", "Loops", "Functions",
        "Arrays and Lists", "Strings", "Introduction to OOP",
        "Beginner Project"
    };

    private static final String[] INTERMEDIATE_TITLES = {
        "Object-Oriented Programming Deep Dive", "Error Handling",
        "Collections and Data Structures", "File I/O",
        "Modules and Packages", "Functional Programming Basics",
        "Recursion", "Interfaces and Abstractions",
        "Generics and Templates", "Intermediate Project"
    };

    private static final String[] ADVANCED_TITLES = {
        "Design Patterns in Practice", "Concurrency and Threads",
        "Memory Management", "Testing",
        "Performance and Optimization", "Advanced OOP",
        "Working with APIs", "Databases and Persistence",
        "Deployment Basics", "Advanced Project"
    };

    private final LearningTopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final AIService aiService;
    private final SeedingStatus seedingStatus;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LessonSeeder(LearningTopicRepository topicRepository,
                        LessonRepository lessonRepository,
                        AIService aiService,
                        SeedingStatus seedingStatus) {
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.aiService = aiService;
        this.seedingStatus = seedingStatus;
    }

    @Override
    public void run(String... args) {
        Thread thread = new Thread(this::seedAllLessons, "lesson-seeder");
        thread.setDaemon(true);
        thread.start();
    }

    private void seedAllLessons() {
        System.out.println("=== LessonSeeder: Starting background generation of 120 lessons ===");
        long totalStart = System.currentTimeMillis();

        List<LearningTopic> topics = topicRepository.findByCategory("LANGUAGE");
        if (topics.isEmpty()) {
            System.out.println("=== LessonSeeder: No topics found, skipping ===");
            seedingStatus.markComplete();
            return;
        }

        for (LearningTopic topic : topics) {
            String language = topic.getName();
            System.out.println("LessonSeeder: Generating lessons for " + language);
            seedLevel(topic, language, "beginner", BEGINNER_TITLES);
            seedLevel(topic, language, "intermediate", INTERMEDIATE_TITLES);
            seedLevel(topic, language, "advanced", ADVANCED_TITLES);
        }

        long elapsed = (System.currentTimeMillis() - totalStart) / 1000;
        System.out.println("=== LessonSeeder: All lessons generated in " + elapsed + "s ===");
        seedingStatus.markComplete();
    }

    private void seedLevel(LearningTopic topic, String language, String level, String[] titles) {
        for (int i = 0; i < 10; i++) {
            int lessonNumber = i + 1;
            String title = titles[i];

            if (lessonRepository.findByTopicIdAndLanguageAndLevelAndLessonNumber(
                    topic.getId(), language, level, lessonNumber).isPresent()) {
                System.out.println("SKIP: " + language + "/" + level + "/" + lessonNumber + " already exists");
                continue;
            }

            try {
                System.out.println("GENERATING: " + language + "/" + level + "/" + lessonNumber + " - " + title);
                String contentJson = aiService.generateLessonContent(language, level, lessonNumber, title);
                if (contentJson == null || contentJson.startsWith("Error")) {
                    System.err.println("FAILED: " + language + "/" + level + "/" + lessonNumber + " - " + contentJson);
                    retry(topic, language, level, lessonNumber, title);
                    continue;
                }

                Lesson lesson = buildLesson(topic, language, level, lessonNumber, title, contentJson);
                lessonRepository.save(lesson);
                System.out.println("SAVED: " + language + "/" + level + "/" + lessonNumber);

                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("ERROR: " + language + "/" + level + "/" + lessonNumber + " - " + e.getMessage());
                retry(topic, language, level, lessonNumber, title);
            }
        }
    }

    private void retry(LearningTopic topic, String language, String level, int lessonNumber, String title) {
        try {
            Thread.sleep(3000);
            System.out.println("RETRY: " + language + "/" + level + "/" + lessonNumber);
            String contentJson = aiService.generateLessonContent(language, level, lessonNumber, title);
            if (contentJson != null && !contentJson.startsWith("Error")) {
                Lesson lesson = buildLesson(topic, language, level, lessonNumber, title, contentJson);
                lessonRepository.save(lesson);
                System.out.println("RETRY SUCCESS: " + language + "/" + level + "/" + lessonNumber);
            } else {
                System.err.println("RETRY FAILED: " + language + "/" + level + "/" + lessonNumber + " - " + contentJson);
            }
        } catch (Exception ex) {
            System.err.println("RETRY ERROR: " + language + "/" + level + "/" + lessonNumber + " - " + ex.getMessage());
        }
    }

    private Lesson buildLesson(LearningTopic topic, String language, String level, int lessonNumber, String title, String contentJson) {
        String summary = "Lesson " + lessonNumber + " of " + level + " level for " + language;
        int estimatedMinutes = 10;

        try {
            JsonNode root = objectMapper.readTree(contentJson);
            if (root.has("summary")) {
                summary = root.get("summary").asText();
            }
            if (root.has("estimatedMinutes")) {
                estimatedMinutes = root.get("estimatedMinutes").asInt();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not parse JSON for " + language + "/" + level + "/" + lessonNumber);
        }

        return Lesson.builder()
                .topic(topic)
                .language(language)
                .level(level)
                .lessonNumber(lessonNumber)
                .title(title)
                .summary(summary)
                .contentJson(contentJson)
                .estimatedMinutes(estimatedMinutes)
                .build();
    }
}
