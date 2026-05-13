package com.codeTutor.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.Category;
import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.repository.CategoryRepository;
import com.codeTutor.backend.repository.LearningTopicRepository;

/**
 * Seeds the database with categories and learning topics on application startup.
 * Only runs if the categories table is empty (prevents duplicates on redeploy).
 */
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
        // Check if data needs to be re-seeded (Languages category must exist with LANGUAGE type topics)
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

        // ═══ CATEGORY 1: Languages ═══
        Category languages = categoryRepository.save(
            Category.builder().name("Languages").icon("code").orderIndex(1).build()
        );
        topicRepository.saveAll(List.of(
            buildTopic("Python Basics", "LANGUAGE", "BEGINNER", languages, 1),
            buildTopic("Java Basics", "LANGUAGE", "BEGINNER", languages, 2),
            buildTopic("JavaScript Basics", "LANGUAGE", "BEGINNER", languages, 3)
        ));

        // ═══ CATEGORY 2: Data Structures ═══
        Category dataStructures = categoryRepository.save(
            Category.builder().name("Data Structures").icon("binary-tree").orderIndex(2).build()
        );
        topicRepository.saveAll(List.of(
            buildTopic("Arrays", "DATA_STRUCTURE", "BEGINNER", dataStructures, 1),
            buildTopic("Linked Lists", "DATA_STRUCTURE", "BEGINNER", dataStructures, 2),
            buildTopic("Stacks", "DATA_STRUCTURE", "BEGINNER", dataStructures, 3),
            buildTopic("Queues", "DATA_STRUCTURE", "BEGINNER", dataStructures, 4),
            buildTopic("Trees", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures, 5),
            buildTopic("Graphs", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures, 6),
            buildTopic("Hash Maps", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures, 7),
            buildTopic("AVL Trees", "DATA_STRUCTURE", "ADVANCED", dataStructures, 8),
            buildTopic("Tries", "DATA_STRUCTURE", "ADVANCED", dataStructures, 9)
        ));

        // ═══ CATEGORY 3: Design Patterns ═══
        Category designPatterns = categoryRepository.save(
            Category.builder().name("Design Patterns").icon("puzzle").orderIndex(3).build()
        );
        topicRepository.saveAll(List.of(
            buildTopic("Singleton", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns, 1),
            buildTopic("Factory Method", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns, 2),
            buildTopic("Observer", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns, 3),
            buildTopic("Command", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns, 4),
            buildTopic("Facade", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns, 5)
        ));

        // ═══ CATEGORY 4: OOP ═══
        Category oop = categoryRepository.save(
            Category.builder().name("OOP").icon("cube").orderIndex(4).build()
        );
        topicRepository.saveAll(List.of(
            buildTopic("Classes and Objects", "OOP", "BEGINNER", oop, 1),
            buildTopic("Inheritance", "OOP", "BEGINNER", oop, 2),
            buildTopic("Polymorphism", "OOP", "INTERMEDIATE", oop, 3),
            buildTopic("Encapsulation", "OOP", "BEGINNER", oop, 4),
            buildTopic("Abstraction", "OOP", "INTERMEDIATE", oop, 5)
        ));

        // ═══ CATEGORY 5: Algorithms ═══
        Category algorithms = categoryRepository.save(
            Category.builder().name("Algorithms").icon("arrow-shuffle").orderIndex(5).build()
        );
        topicRepository.saveAll(List.of(
            buildTopic("Recursion", "ALGORITHM", "INTERMEDIATE", algorithms, 1),
            buildTopic("Sorting Algorithms", "ALGORITHM", "INTERMEDIATE", algorithms, 2),
            buildTopic("Binary Search", "ALGORITHM", "INTERMEDIATE", algorithms, 3),
            buildTopic("Big O Notation", "ALGORITHM", "BEGINNER", algorithms, 4),
            buildTopic("Dynamic Programming", "ALGORITHM", "ADVANCED", algorithms, 5)
        ));

        long catCount = categoryRepository.count();
        long topicCount = topicRepository.count();
        System.out.println("Seeding complete: " + catCount + " categories, " + topicCount + " topics inserted");
    }

    private LearningTopic buildTopic(String name, String category, String difficulty, Category categoryEntity, int orderIndex) {
        return LearningTopic.builder()
                .name(name)
                .category(category)
                .difficulty(difficulty)
                .categoryEntity(categoryEntity)
                .description(name + " - " + difficulty.toLowerCase() + " level")
                .build();
    }
}
