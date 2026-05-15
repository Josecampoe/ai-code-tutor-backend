package com.codeTutor.backend.config;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.model.Lesson;
import com.codeTutor.backend.repository.LearningTopicRepository;
import com.codeTutor.backend.repository.LessonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class LessonSeeder implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final LearningTopicRepository topicRepository;
    private final ObjectMapper objectMapper;

    private static final String[] LANGUAGES =
        {"python", "java", "javascript", "typescript"};
    private static final String[] LEVELS =
        {"beginner", "intermediate", "advanced"};
    private static final int LESSONS_PER_LEVEL = 10;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long count = lessonRepository.count();
        if (count > 0) {
            log.info("Lessons already seeded ({}). Skipping.", count);
            return;
        }

        log.info("Starting lesson seeder from JSON files...");
        int saved = 0;

        for (String language : LANGUAGES) {
            Optional<LearningTopic> topicOpt =
                topicRepository.findByNameContainingIgnoreCase(language);

            if (topicOpt.isEmpty()) {
                log.error("Topic not found for: {}", language);
                continue;
            }

            LearningTopic topic = topicOpt.get();

            for (String level : LEVELS) {
                for (int n = 1; n <= LESSONS_PER_LEVEL; n++) {
                    String path = String.format(
                        "/lessons/%s/%s/lesson%d.json",
                        language, level, n
                    );

                    try {
                        InputStream is =
                            getClass().getResourceAsStream(path);

                        if (is == null) {
                            log.warn("File not found: {}", path);
                            continue;
                        }

                        JsonNode json = objectMapper.readTree(is);

                        Lesson lesson = new Lesson();
                        lesson.setTopic(topic);
                        lesson.setLanguage(language);
                        lesson.setLevel(level);
                        lesson.setLessonNumber(n);
                        lesson.setTitle(json.get("title").asText());
                        lesson.setSummary(json.get("summary").asText());
                        lesson.setEstimatedMinutes(
                            json.get("estimatedMinutes").asInt()
                        );
                        lesson.setContentJson(
                            json.get("sections").toString()
                        );

                        lessonRepository.save(lesson);
                        saved++;

                        log.info("Saved: {} {} lesson {}", language, level, n);

                    } catch (Exception e) {
                        log.error("Error loading {}: {}", path, e.getMessage());
                    }
                }
            }
        }

        log.info("Seeder complete. Saved {} lessons.", saved);
    }
}
