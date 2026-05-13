package com.codeTutor.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LearningTopicRepository topicRepository;

    @Override
    public void run(String... args) {
        // Re-seed if categories are empty (first deploy or after DB reset)
        if (categoryRepository.count() > 0) {
            System.out.println("[DataSeeder] Data already exists, skipping seed.");
            return;
        }

        // Clear old topics without categories (from previous seeder version)
        topicRepository.deleteAll();

        // ═══════════════════════════════════════════
        // CATEGORY 1: Languages
        // ═══════════════════════════════════════════
        Category languages = categoryRepository.save(
            Category.builder().name("Languages").description("Learn programming languages from scratch").icon("code").orderIndex(0).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Python", "DATA_STRUCTURE", "BEGINNER", languages,
                "Learn Python from zero. Variables, data types, control flow, functions, and basic I/O. The most beginner-friendly language."),
            buildTopic("Java", "DATA_STRUCTURE", "BEGINNER", languages,
                "Learn Java fundamentals. Classes, methods, data types, control structures, and object-oriented basics."),
            buildTopic("JavaScript", "DATA_STRUCTURE", "BEGINNER", languages,
                "Learn JavaScript for web development. Variables, functions, DOM manipulation, events, and async programming."),
            buildTopic("TypeScript", "DATA_STRUCTURE", "INTERMEDIATE", languages,
                "Learn TypeScript, a typed superset of JavaScript. Interfaces, generics, type guards, and advanced type system features.")
        ));

        // ═══════════════════════════════════════════
        // CATEGORY 2: Data Structures
        // ═══════════════════════════════════════════
        Category dataStructures = categoryRepository.save(
            Category.builder().name("Data Structures").description("Fundamental data structures for efficient programming").icon("binary-tree").orderIndex(1).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Arrays", "DATA_STRUCTURE", "BEGINNER", dataStructures,
                "Contiguous memory storage. Learn indexing, iteration, insertion, deletion, and common array algorithms like two pointers and sliding window."),
            buildTopic("Linked Lists", "DATA_STRUCTURE", "BEGINNER", dataStructures,
                "Nodes connected by pointers. Singly and doubly linked lists, insertion, deletion, reversal, and cycle detection."),
            buildTopic("Stacks", "DATA_STRUCTURE", "BEGINNER", dataStructures,
                "LIFO data structure. Push, pop, peek operations. Used in expression evaluation, undo systems, and backtracking."),
            buildTopic("Queues", "DATA_STRUCTURE", "BEGINNER", dataStructures,
                "FIFO data structure. Enqueue, dequeue operations. Used in BFS, task scheduling, and message processing."),
            buildTopic("Hash Maps", "DATA_STRUCTURE", "BEGINNER", dataStructures,
                "Key-value storage with O(1) average lookup. Hash functions, collision handling, and practical applications like frequency counting."),
            buildTopic("Trees", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures,
                "Hierarchical data structure. Binary trees, BST, traversals (inorder, preorder, postorder), and balanced trees."),
            buildTopic("Graphs", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures,
                "Nodes and edges representing relationships. Adjacency list/matrix, directed/undirected, weighted graphs."),
            buildTopic("Heaps", "DATA_STRUCTURE", "INTERMEDIATE", dataStructures,
                "Complete binary tree with heap property. Min-heap, max-heap, priority queues, and heapsort algorithm.")
        ));

        // ═══════════════════════════════════════════
        // CATEGORY 3: Design Patterns
        // ═══════════════════════════════════════════
        Category designPatterns = categoryRepository.save(
            Category.builder().name("Design Patterns").description("Reusable solutions to common software design problems").icon("puzzle").orderIndex(2).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Singleton", "DESIGN_PATTERN", "BEGINNER", designPatterns,
                "Ensures a class has only one instance with a global access point. Thread-safe implementations and when to use vs avoid it."),
            buildTopic("Factory Method", "DESIGN_PATTERN", "BEGINNER", designPatterns,
                "Creates objects without specifying the exact class. Defines an interface for creation, letting subclasses decide which class to instantiate."),
            buildTopic("Observer", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns,
                "Defines a one-to-many dependency. When one object changes state, all dependents are notified. Foundation of event systems and reactive programming."),
            buildTopic("Strategy", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns,
                "Encapsulates interchangeable algorithms. Lets the algorithm vary independently from clients that use it. Eliminates complex conditionals."),
            buildTopic("Decorator", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns,
                "Attaches additional responsibilities to objects dynamically. Provides a flexible alternative to subclassing for extending functionality."),
            buildTopic("Facade", "DESIGN_PATTERN", "BEGINNER", designPatterns,
                "Provides a simplified interface to a complex subsystem. Reduces coupling between clients and internal components."),
            buildTopic("Command", "DESIGN_PATTERN", "INTERMEDIATE", designPatterns,
                "Encapsulates a request as an object. Enables undo/redo, queuing, logging, and parameterization of actions."),
            buildTopic("Builder", "DESIGN_PATTERN", "BEGINNER", designPatterns,
                "Constructs complex objects step by step. Separates construction from representation, avoiding telescoping constructors.")
        ));

        // ═══════════════════════════════════════════
        // CATEGORY 4: OOP
        // ═══════════════════════════════════════════
        Category oop = categoryRepository.save(
            Category.builder().name("OOP").description("Object-Oriented Programming principles and concepts").icon("cube").orderIndex(3).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Classes & Objects", "DESIGN_PATTERN", "BEGINNER", oop,
                "The building blocks of OOP. Learn how to define classes, create objects, use constructors, and organize code into reusable blueprints."),
            buildTopic("Inheritance", "DESIGN_PATTERN", "BEGINNER", oop,
                "Create new classes based on existing ones. Understand superclass/subclass relationships, method overriding, and the 'is-a' relationship."),
            buildTopic("Polymorphism", "DESIGN_PATTERN", "INTERMEDIATE", oop,
                "One interface, multiple implementations. Method overloading, overriding, and how polymorphism enables flexible and extensible code."),
            buildTopic("Encapsulation", "DESIGN_PATTERN", "BEGINNER", oop,
                "Bundle data and methods together, hiding internal state. Access modifiers (public, private, protected) and getter/setter patterns."),
            buildTopic("Abstraction", "DESIGN_PATTERN", "INTERMEDIATE", oop,
                "Hide complex implementation details behind simple interfaces. Abstract classes, interfaces, and designing clean APIs."),
            buildTopic("Interfaces", "DESIGN_PATTERN", "INTERMEDIATE", oop,
                "Define contracts that classes must implement. Multiple inheritance through interfaces, default methods, and dependency inversion.")
        ));

        // ═══════════════════════════════════════════
        // CATEGORY 5: Algorithms
        // ═══════════════════════════════════════════
        Category algorithms = categoryRepository.save(
            Category.builder().name("Algorithms").description("Essential algorithms for problem solving").icon("arrow-shuffle").orderIndex(4).build()
        );

        topicRepository.saveAll(List.of(
            buildTopic("Recursion", "ALGORITHM", "BEGINNER", algorithms,
                "Functions that call themselves. Base cases, recursive cases, call stack visualization. Solve factorial, Fibonacci, and tree problems."),
            buildTopic("Binary Search", "ALGORITHM", "BEGINNER", algorithms,
                "Efficient O(log n) search in sorted arrays. Learn the divide-and-conquer approach and common variations like lower/upper bound."),
            buildTopic("Bubble Sort", "ALGORITHM", "BEGINNER", algorithms,
                "Simple comparison-based sorting. Understand swapping, passes, and optimization with early termination. O(n²) complexity."),
            buildTopic("Merge Sort", "ALGORITHM", "INTERMEDIATE", algorithms,
                "Divide-and-conquer sorting in O(n log n). Split, sort recursively, and merge. Stable sort with guaranteed performance."),
            buildTopic("Quick Sort", "ALGORITHM", "INTERMEDIATE", algorithms,
                "Efficient in-place sorting using pivots. Partition, recurse on subarrays. Average O(n log n), widely used in practice."),
            buildTopic("BFS & DFS", "ALGORITHM", "INTERMEDIATE", algorithms,
                "Graph traversal algorithms. Breadth-First Search uses queues for shortest paths. Depth-First Search uses stacks for exploration."),
            buildTopic("Dynamic Programming", "ALGORITHM", "ADVANCED", algorithms,
                "Solve complex problems by breaking them into overlapping subproblems. Memoization, tabulation, and classic problems like knapsack and LCS.")
        ));

        System.out.println("[DataSeeder] Categories and topics seeded successfully (" + topicRepository.count() + " topics).");
    }

    private LearningTopic buildTopic(String name, String category, String difficulty, Category categoryEntity, String description) {
        return LearningTopic.builder()
                .name(name)
                .category(category)
                .difficulty(difficulty)
                .categoryEntity(categoryEntity)
                .description(description)
                .build();
    }
}
