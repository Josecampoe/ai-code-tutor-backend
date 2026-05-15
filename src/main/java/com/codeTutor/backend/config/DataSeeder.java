package com.codeTutor.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.Category;
import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.repository.CategoryRepository;
import com.codeTutor.backend.repository.LearningTopicRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final LearningTopicRepository topicRepository;

    public DataSeeder(CategoryRepository categoryRepository, LearningTopicRepository topicRepository) {
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        boolean needsReseed = categoryRepository.count() == 0
                || topicRepository.count() == 0
                || topicRepository.findAll().stream().noneMatch(t -> "LANGUAGE".equals(t.getCategory()));

        if (!needsReseed) {
            System.out.println("Database already has correct data, skipping seed.");
            return;
        }

        System.out.println("Seeding database...");
        topicRepository.deleteAll();
        categoryRepository.deleteAll();

        Category languages = categoryRepository.save(
            Category.builder().name("Languages").icon("code").orderIndex(1).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Python", "LANGUAGE", languages, 1),
            buildTopic("Java", "LANGUAGE", languages, 2),
            buildTopic("JavaScript", "LANGUAGE", languages, 3),
            buildTopic("TypeScript", "LANGUAGE", languages, 4)
        ));

        long catCount = categoryRepository.count();
        long topicCount = topicRepository.count();
        System.out.println("Seeding complete: " + catCount + " categories, " + topicCount + " topics inserted");
    }

    private LearningTopic buildTopic(String name, String category, Category categoryEntity, int orderIndex) {
        return LearningTopic.builder()
                .name(name)
                .category(category)
                .difficulty("BEGINNER")
                .categoryEntity(categoryEntity)
                .description("Learn " + name + " from zero to advanced")
                .build();
    }
}
